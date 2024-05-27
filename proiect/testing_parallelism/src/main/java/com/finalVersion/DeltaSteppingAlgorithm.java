package com.finalVersion;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.alg.sp.SingleSourceShortestPath;
import org.graph4j.util.Path;

import org.graph4j.Edge;

/**
 * Implementation of the Delta-Stepping algorithm for single-source shortest
 * path in parallel.
 * <p>
 * This implementation is based on the Delta-Stepping algorithm as described in
 * the paper:
 * Meyer, Ulrich, and Peter Sanders. "Δ-stepping: a parallelizable shortest path
 * algorithm."
 * Journal of Algorithms 49, no. 1 (2003): 114-152.
 * </p>
 * 
 * @author Morari V. Maxim
 */
public class DeltaSteppingAlgorithm extends GraphAlgorithm implements SingleSourceShortestPath {
    private boolean computed;
    private Double delta;
    private final Map<Integer, Set<Integer>> buckets;
    private int currentBucketIndex;
    private double[] tentative;
    private int[] parentOnPath;
    private final int n;
    private int maxBucketIndex;
    private final int threadsNum;
    private ExecutorService executor;

    int source;
    Map<Integer, List<Edge<?>>> lightEdges;
    Map<Integer, List<Edge<?>>> heavyEdges;

    private List<Lock> vertexLocks;

    /**
     * Constructs a DeltaSteppingAlgorithm instance.
     *
     * @param graph        the graph to run the algorithm on
     * @param source       the source vertex
     * @param numOfThreads the number of threads to use
     */
    public DeltaSteppingAlgorithm(Graph<?, ?> graph, int source, int numOfThreads) {
        super(graph);
        this.source = source;
        threadsNum = numOfThreads;

        buckets = new ConcurrentHashMap<>();
        currentBucketIndex = 0;
        maxBucketIndex = 0;

        n = graph.numVertices();

        tentative = new double[n];
        parentOnPath = new int[n];

        Arrays.fill(tentative, Double.POSITIVE_INFINITY);
        Arrays.fill(parentOnPath, -1);

        lightEdges = new ConcurrentHashMap<>();
        heavyEdges = new ConcurrentHashMap<>();

        vertexLocks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vertexLocks.add(new ReentrantLock());
        }
    }

    /**
     * Finds the first non-empty bucket.
     * 
     * Given the currentBucketIndex, it find the next NE bucket by checking if the
     * buckets collection contains the bucket for an index up to maxBucketIndex.
     * maxBucketIndex get set when the relax method creates a new bucket.
     * 
     * The next NE bucket's index can't be lower than the current one as there are
     * no negative arcs, so that vertices can be inserted only in buckets with index
     * greater or equal to the current one.
     *
     * @return a set of vertices in the first non-empty bucket
     */
    private Set<Integer> getFirstNonEmptyBucket() {
        while (!buckets.containsKey(currentBucketIndex) || buckets.get(currentBucketIndex).isEmpty()) {
            currentBucketIndex++;
            if (currentBucketIndex > maxBucketIndex)
                return Collections.emptySet();
        }
        Set<Integer> bucket = buckets.get(currentBucketIndex);

        buckets.remove(currentBucketIndex);
        return bucket;
    }

    /**
     * Computes the bucket index for a given distance.
     *
     * @param distance the distance to compute the bucket index for
     * @return the bucket index
     */
    private int getBucketIndex(Double distance) {
        return (int) (distance / delta);
    }

    @Override
    public double[] getPathWeights() {
        return tentative;
    }

    @Override
    public double getPathWeight(int target) {
        return tentative[target];
    }

    /**
     * Distributes vertices evenly among threads and gives each a light or heavy
     * relaxation task.
     * 
     * It uses a CountDownLatch to wait for the completion of all tasks. Each thread
     * executes latch.countDown() after completion.
     *
     * @param vertices         the vertices to distribute
     * @param isLightEdgesTask true if the task is for light edges
     * @throws InterruptedException if thread execution is interrupted, caused by
     *                              latch.await()
     */
    void distributeVerticesAndExecute(Set<Integer> vertices, boolean isLightEdgesTask) throws InterruptedException {
        int remainingNodes = vertices.size();
        int remainingThreads = threadsNum;
        if (remainingNodes < remainingThreads)
            remainingThreads = remainingNodes;
        Iterator<Integer> verticesIterator = vertices.iterator();

        CountDownLatch latch = new CountDownLatch(remainingThreads);

        while (remainingNodes > 0) {
            int allottedNumber = remainingNodes / remainingThreads;

            int[] list = new int[allottedNumber];

            for (int i = 0; i < allottedNumber; i++) {
                list[i] = verticesIterator.next();
            }

            executor.submit(new RelaxTask(list, this, isLightEdgesTask, latch));
            remainingNodes -= allottedNumber;
            remainingThreads -= 1;
        }

        latch.await();
    }

    /**
     * Checks if the algorithm has been computed, and computes it if necessary.
     *
     * @throws InterruptedException               if thread execution is interrupted
     * @throws InvalidAlgorithmParameterException if the an edge of the graph is
     *                                            negative
     */
    private void checkComputed() throws InterruptedException, InvalidAlgorithmParameterException {
        if (!computed) {
            solve();
        }
        computed = true;
    }

    /**
     * Finds the shortest path to a target vertex.
     *
     * @param target the target vertex
     * @return the path to the target vertex
     */
    public Path findPath(int target) {
        try {
            checkComputed();
            LinkedList<Integer> path = new LinkedList<>();

            while (parentOnPath[target] != -1) {
                path.addFirst(target);
                target = parentOnPath[target];
            }

            path.addFirst(target);

            return new Path(graph, path.stream().mapToInt(Integer::intValue).toArray());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Effectively runs the algorithm to compute shortest paths from the source to
     * all other vertices of the graph.
     * 
     * First, it computes the delta as the average weight of all arcs. Then it
     * initialises the light and heavy edges and checks if there are negative edges.
     * Then in continuously searches the next non-empty bucket until there is none
     * left.
     * 
     * For each bucket, it finds light relaxation request, distributes them among
     * all threads, remebers all vertices (in order to find heavy relaxation request
     * later on) and clears the current bucket. During a phase (when threads relax
     * request) vertices can be reinserted in the current bucket. When the current
     * bucket is finnally empty, heavy relaxation requests are executed.
     *
     * @throws InterruptedException               if thread execution is interrupted
     * @throws InvalidAlgorithmParameterException if the an edge of the graph is
     *                                            negative
     */
    public void solve() throws InterruptedException, InvalidAlgorithmParameterException {
        delta = new GraphAverageWeightCalculator(graph).computeAverageEdgeWeight(threadsNum);

        initialiseLightAndHeavyEdges();
        relax(source, 0.0, -1);

        executor = Executors.newFixedThreadPool(threadsNum);

        while (true) {
            Set<Integer> currentBucket = getFirstNonEmptyBucket();

            if (currentBucket.isEmpty())
                break;

            Set<Integer> R = new HashSet<>();

            while (!currentBucket.isEmpty()) {
                distributeVerticesAndExecute(currentBucket, true);
                R.addAll(currentBucket);
                currentBucket.clear();
            }

            distributeVerticesAndExecute(R, false);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /**
     * Finds heavy or light relaxation requests for a set of nodes.
     * 
     * @param nodes   the nodes to find requests for
     * @param isLight true if the requests are for light edges, false otherwise
     * @return a list of requests
     */
    List<Request> findRequests(Collection<Integer> nodes, boolean isLight) {
        List<Request> requests = new LinkedList<>();

        var edges = heavyEdges;

        if (isLight)
            edges = lightEdges;

        for (int vertex : nodes) {
            if (edges.containsKey(vertex))
                for (Edge<?> edge : edges.get(vertex)) {
                    requests.add(new Request(edge.target(), tentative[vertex] + edge.weight(), vertex));
                }
        }

        return requests;
    }

    List<Request> findRequests(int[] nodes, boolean isLight) {
        List<Request> requests = new LinkedList<>();

        var edges = heavyEdges;

        if (isLight)
            edges = lightEdges;

        for (int vertex : nodes) {
            if (edges.containsKey(vertex))
                for (Edge<?> edge : edges.get(vertex)) {
                    requests.add(new Request(edge.target(), tentative[vertex] + edge.weight(), vertex));
                }
        }

        return requests;
    }

    /**
     * Relaxes the edges based on the given requests.
     *
     * @param requests the requests to relax
     */
    private void relaxRequest(List<Request> requests) {
        for (Request request : requests) {
            relax(request.getVertex(), request.getDistance(), request.getFrom());
        }
    }

    /**
     * Relaxes a node with a new distance and parent.
     * 
     * Relaxing consists in checking if the given newDistance is shorter than the
     * current tentative distance. In case it is, it removes the vertex from its
     * bucket and inserts it another one.
     * It also sets the maxBucketIndex, which is max(maxBucketIndex,
     * newBucketIndex), and the parentOnPath array, which contains the parents on
     * the shortest path from source to the respective vertex.
     *
     * @param node        the node to relax
     * @param newDistance the new distance
     * @param parent      the parent node
     */
    private void relax(int node, Double newDistance, int parent) {
        Lock lock = vertexLocks.get(node);
        lock.lock();
        if (newDistance < tentative[node]) {
            int toBucketIndex = getBucketIndex(newDistance);

            buckets.computeIfPresent(getBucketIndex(tentative[node]), (k, bucket) -> {
                bucket.remove(node);
                return bucket;
            });

            buckets.computeIfAbsent(toBucketIndex, k -> new ConcurrentSkipListSet<>()).add(node);
            tentative[node] = newDistance;
            parentOnPath[node] = parent;
            maxBucketIndex = Math.max(maxBucketIndex, toBucketIndex);
        }
        lock.unlock();
    }

    /**
     * Initializes light and heavy edges based on the delta value, but also check
     * for negative edges
     *
     * @throws InvalidAlgorithmParameterException if the an edge of the graph is
     *                                            negative
     */
    private void initialiseLightAndHeavyEdges() throws InvalidAlgorithmParameterException {
        var vertices = graph.vertices();
        ForkJoinPool pool = new ForkJoinPool();
        InitializationAndValidationTask task = new InitializationAndValidationTask(vertices, graph, delta, lightEdges,
                heavyEdges, 0, vertices.length, vertices.length / threadsNum + 1);
        pool.invoke(task);

        if (!task.validInput())
            throw new InvalidAlgorithmParameterException("Edge weight cannot be negative");
    }

    @Override
    public int getSource() {
        return source;
    }

    /**
     * A task for relaxing edges.
     * 
     * It find requests and relaxes them for a given vertex array.
     */
    class RelaxTask implements Runnable {
        int[] requestNodes;
        DeltaSteppingAlgorithm parentAlgorithm;
        boolean isLight;
        CountDownLatch latch;

        public RelaxTask(int[] requestNodes, DeltaSteppingAlgorithm parentAlgorithm, boolean isLight,
                CountDownLatch latch) {
            this.requestNodes = requestNodes;
            this.parentAlgorithm = parentAlgorithm;
            this.isLight = isLight;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                parentAlgorithm.relaxRequest(parentAlgorithm.findRequests(requestNodes, isLight));
            } catch (Exception e) {
                System.err.println("Task failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        }
    }

    /**
     * A relaxation request contains the vertex whose distance from the source is to
     * be settled, the potentially shortest distance form the source and the parent
     * on the respective path.
     */
    class Request {
        int vertex;
        double distance;
        int from;

        public Request(int vertex, double distance, int from) {
            this.vertex = vertex;
            this.distance = distance;
            this.from = from;
        }

        public int getVertex() {
            return vertex;
        }

        public double getDistance() {
            return distance;
        }

        public int getFrom() {
            return from;
        }

    }

    /**
     * A task for initializing light and heavy edges and checking for negative
     * edges.
     * 
     * It does so by recursively dividing the work to all threads.
     */
    class InitializationAndValidationTask extends RecursiveAction {
        private final int threshold;
        private final int[] vertices;
        private final Graph<?, ?> graph;
        private final double delta;
        private final Map<Integer, List<Edge<?>>> lightEdges;
        private final Map<Integer, List<Edge<?>>> heavyEdges;
        private final int start;
        private final int end;
        private Exception exception;

        boolean validInput() {
            return exception == null;
        }

        public InitializationAndValidationTask(int[] vertices, Graph<?, ?> graph, double delta,
                Map<Integer, List<Edge<?>>> lightEdges, Map<Integer, List<Edge<?>>> heavyEdges, int start, int end,
                int threshold) {
            this.vertices = vertices;
            this.graph = graph;
            this.delta = delta;
            this.lightEdges = lightEdges;
            this.heavyEdges = heavyEdges;
            this.start = start;
            this.end = end;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            try {
                if (end - start <= threshold) {
                    processVertices();
                } else {
                    int mid = start + (end - start) / 2;
                    InitializationAndValidationTask leftTask = new InitializationAndValidationTask(vertices, graph,
                            delta,
                            lightEdges, heavyEdges, start, mid, threshold);
                    InitializationAndValidationTask rightTask = new InitializationAndValidationTask(vertices, graph,
                            delta,
                            lightEdges, heavyEdges, mid, end, threshold);
                    invokeAll(leftTask, rightTask);
                    leftTask.join();
                    rightTask.join();
                    if (leftTask.exception != null) {
                        throw leftTask.exception;
                    }
                    if (rightTask.exception != null) {
                        throw rightTask.exception;
                    }
                }
            } catch (Exception e) {
                exception = e;
            }
        }

        private void processVertices() throws InvalidAlgorithmParameterException {
            for (int i = start; i < end; i++) {
                int vertex = vertices[i];
                for (Edge<?> edge : graph.edgesOf(vertex)) {
                    if (edge.weight() < 0.0)
                        throw new InvalidAlgorithmParameterException("Edge weight cannot be negative");

                    Map<Integer, List<Edge<?>>> targetMap = (edge.weight() <= delta) ? lightEdges : heavyEdges;
                    targetMap.computeIfAbsent(edge.source(), k -> new LinkedList<>()).add(edge);
                }
            }
        }
    }

    /**
     * This class computes the average weight of all edges of a class by dividing
     * the computation of the sum of all edges evenly among threads, joining their
     * results and finally dividing it by the edge count.
     */

    class GraphAverageWeightCalculator {

        private final Graph<?, ?> graph;

        public GraphAverageWeightCalculator(Graph<?, ?> graph) {
            this.graph = graph;
        }

        private class TotalWeightTask extends RecursiveTask<Double> {
            private final Edge<?>[] edges;
            private final int start;
            private final int end;
            private final int threshold; // Adjust the threshold as needed

            public TotalWeightTask(Edge<?>[] edges, int start, int end, int threshold) {
                this.edges = edges;
                this.start = start;
                this.end = end;
                this.threshold = threshold;
            }

            @Override
            protected Double compute() {
                if (end - start <= threshold) {
                    double total = 0.0;
                    for (int i = start; i < end; i++) {
                        total += edges[i].weight();
                    }
                    return total;
                } else {
                    int mid = (start + end) / 2;
                    TotalWeightTask leftTask = new TotalWeightTask(edges, start, mid, threshold);
                    TotalWeightTask rightTask = new TotalWeightTask(edges, mid, end, threshold);
                    leftTask.fork();
                    double rightResult = rightTask.compute();
                    double leftResult = leftTask.join();
                    return leftResult + rightResult;
                }
            }
        }

        public Double computeAverageEdgeWeight(int numbThreads) {
            var edges = graph.edges();
            int edgeCount = edges.length;
            if (edgeCount == 0) {
                return 0.0;
            }

            ForkJoinPool pool = new ForkJoinPool();
            TotalWeightTask task = new TotalWeightTask(edges, 0, edgeCount, edgeCount / numbThreads + 1);
            double totalWeight = pool.invoke(task);

            return totalWeight / edgeCount;
        }
    }

}
