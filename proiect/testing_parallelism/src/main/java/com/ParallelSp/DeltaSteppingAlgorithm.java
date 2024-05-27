package com.ParallelSp;

import java.io.PrintStream;
import java.security.InvalidAlgorithmParameterException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;

import org.graph4j.Graph;
import org.graph4j.alg.sp.SingleSourceShortestPath;
import org.graph4j.util.Path;
import com.parallelTest.GraphAverageWeightCalculator;

import org.graph4j.Edge;

public class DeltaSteppingAlgorithm {
    static String red = "\033[31m";
    // ANSI escape code to reset color
    static String reset = "\033[0m";

    static String green = "\033[32m";

    private Double delta;
    private final Map<Integer, Set<Integer>> buckets;
    private int currentBucketIndex;
    private double[] tentative;
    private int[] parentOnPath;
    private final int n;
    private int maxBucketIndex;
    private final int threadsNum;
    private ExecutorService executor;
    long timeToFindFirstNEBucket = 0;
    long timeToAddToR = 0;
    long timtToSynch = 0;
    long phaseTime = 0;
    long heavyTime = 0;
    long relaxTime = 0;
    long initAndDelta = 0;

    Graph graph;
    int source;
    Map<Integer, List<Edge>> lightEdges;
    Map<Integer, List<Edge>> heavyEdges;

    private List<Lock> vertexLocks;

    public DeltaSteppingAlgorithm(int source, Graph graph, int numOfThreads) {
        this.graph = graph;
        this.source = source;
        threadsNum = numOfThreads;

        buckets = new ConcurrentHashMap<>();
        currentBucketIndex = 0;
        maxBucketIndex = 0;

        n = graph.numVertices();

        tentative = new double[n];
        parentOnPath = new int[n];

        for (int i = 0; i < tentative.length; i++) {
            tentative[i] = Double.POSITIVE_INFINITY;
            parentOnPath[i] = -1;
        }

        lightEdges = new ConcurrentHashMap<>();
        heavyEdges = new ConcurrentHashMap<>();

        vertexLocks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vertexLocks.add(new ReentrantLock());
        }

    }

    public Set<Integer> getFirstNonEmptyBucket() {
        while (!buckets.containsKey(currentBucketIndex) || buckets.get(currentBucketIndex).isEmpty()) {
            currentBucketIndex++;
            if (currentBucketIndex > maxBucketIndex)
                return Collections.emptySet();
        }
        Set<Integer> bucket = buckets.get(currentBucketIndex);

        buckets.remove(currentBucketIndex);
        return bucket;
    }

    private int getBucketIndex(Double distance) {
        return (int) (distance / delta);
    }

    static void setOutput(String outputFilePath) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(outputFilePath));
            System.setOut(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        PrintStream originalOut = System.out;

        // setOutput("deltastepping_out.txt");

        Graph graph = GRParse.parseGRFile("ny.gr");
        // graph.setEdgeWeight(0, 1, -1.0);
        SingleSourceShortestPath shortestPath = SingleSourceShortestPath.getInstance(graph, 0);

        try {

            int testsnum = 10;
            long total = 0;
            long initAndDeltaTotal = 0;

            for (int i = 0; i < testsnum; i++) {
                long startTime = System.nanoTime();
                DeltaSteppingAlgorithm instance = new DeltaSteppingAlgorithm(0, graph, 100);
                instance.solve();

                long endTime = System.nanoTime();

                long duration = (endTime - startTime);

                total += duration;
                initAndDeltaTotal += instance.initAndDelta;

                System.out.println("Time taken to solve the graph: " + duration / 1_000_000 +
                        " ms");
                System.out.println(
                        "without initAndDelta: " + (duration - instance.initAndDelta) / 1_000_000
                                + " ms");

                System.out.println("Max bucket index: " + instance.maxBucketIndex);
                System.out.println("timeToFindFirstNEBucket: " +
                        instance.timeToFindFirstNEBucket / 1_000_000 + " ms");

                System.out.println("timeToAddToRAndClearCurrent: " + instance.timeToAddToR /
                        1_000_000 + " ms");
                System.out.println("timtToSynch: " + instance.timtToSynch / 1_000_000 +
                        " ms");
                System.out.println("phaseTime: " + instance.phaseTime / 1_000_000 + " ms");
                System.out.println("heavyTime: " + instance.heavyTime / 1_000_000 + " ms");
                System.out.println("relaxTime: " + instance.relaxTime / 1_000_000 + " ms");

                boolean ok = true;
                for (var vertex : instance.graph.vertices()) {

                    if (shortestPath.getPathWeight(vertex) != instance.tentative[vertex]) {
                        System.out.println(shortestPath.getPathWeight(vertex));

                        System.out.println(instance.tentative[vertex]);

                        System.out.println(red + "incorrect result" + reset);

                        ok = false;
                        break;

                    }
                }
                if (ok)
                    System.out.println(green + "correct result" + reset);

            }
            System.setOut(originalOut);

            System.out.println("Average time for " + testsnum + " instances: " + total /
                    testsnum / 1_000_000 + " ms");
            System.out.println("Average time(w/o init and delta) for " + testsnum +
                    "instances: "
                    + (total - initAndDeltaTotal) / testsnum / 1_000_000 + " ms");
        } catch (Exception e) {
            System.err.println("Something went wrong");
            e.printStackTrace();
        }

    }

    void distributeVerticesAndExecute(Set<Integer> vertices, boolean isLightEdgesTask) throws InterruptedException {

        long phaseStart = System.nanoTime();
        int remainingNodes = vertices.size();
        int remainingThreads = threadsNum;
        if (remainingNodes < remainingThreads)
            remainingThreads = remainingNodes;
        Iterator<Integer> verticesIterator = vertices.iterator();

        CountDownLatch latch = new CountDownLatch(remainingThreads);

        while (remainingNodes > 0) {
            int alottedNumber = remainingNodes / remainingThreads;

            int[] list = new int[alottedNumber];

            for (int i = 0; i < alottedNumber; i++) {
                list[i] = verticesIterator.next();

            }

            executor.submit(new ParallelTask(list, this, isLightEdgesTask, latch));
            remainingNodes -= alottedNumber;
            remainingThreads -= 1;

        }

        latch.await();
        long phaseEnd = System.nanoTime();

        phaseTime += phaseEnd - phaseStart;

    }

    public Path findPath(int target) {

        LinkedList<Integer> path = new LinkedList<>();

        while (parentOnPath[target] != -1) {
            path.addFirst(target);
            target = parentOnPath[target];
        }

        path.addFirst(target);

        return new Path(graph, path.stream().mapToInt(Integer::intValue).toArray());
    }

    public void solve() throws InterruptedException, InvalidAlgorithmParameterException {

        long deltaComputeStart = System.nanoTime();

        delta = new GraphAverageWeightCalculator(graph).computeAverageEdgeWeight(threadsNum);

        long deltaComputeEnd = System.nanoTime();

        System.out.println("delta: " + delta);

        long initStart = System.nanoTime();
        initialiseLightAndHeavyEdges();
        long initEnd = System.nanoTime();

        relax(source, 0.0, -1);

        System.out.println("initialiseLightAndHeavyEdges:" + (initEnd - initStart) / 1_000_000 + " ms");

        initAndDelta = initEnd - deltaComputeStart;

        System.out.println("Delta compute time: " + (deltaComputeEnd - deltaComputeStart) / 1000000 + " ms");

        executor = Executors.newFixedThreadPool(threadsNum);

        while (true) {

            long startTime = System.nanoTime();
            Set<Integer> currentBucket = getFirstNonEmptyBucket();
            long endTime = System.nanoTime();

            timeToFindFirstNEBucket += (endTime - startTime);

            if (currentBucket.isEmpty())
                break;

            Set<Integer> R = new HashSet<>();

            while (!currentBucket.isEmpty()) {

                distributeVerticesAndExecute(currentBucket, true);

                long start = System.nanoTime();

                R.addAll(currentBucket);

                currentBucket.clear();

                long end = System.nanoTime();

                timeToAddToR += end - start;
            }

            long heavyStart = System.nanoTime();

            distributeVerticesAndExecute(R, false);

            long heavyEnd = System.nanoTime();

            heavyTime += heavyEnd - heavyStart;
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    List<Request> findRequests(Collection<Integer> nodes, boolean isLight) {
        List<Request> requests = new LinkedList<>();

        var edges = heavyEdges;

        if (isLight)
            edges = lightEdges;

        for (int vertex : nodes) {
            if (edges.containsKey(vertex))
                for (Edge edge : edges.get(vertex)) {
                    requests.add(new Request(edge.target(), tentative[vertex] + edge.weight(),
                            vertex));
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
                for (Edge edge : edges.get(vertex)) {
                    requests.add(new Request(edge.target(), tentative[vertex] + edge.weight(),
                            vertex));
                }
        }

        return requests;
    }

    public void relaxRequest(List<Request> requests) {
        for (Request request : requests) {
            relax(request.getVertex(), request.getDistance(), request.getFrom());
        }
    } 

    private void relax(int node, Double newDistance, int parent) {
        long relaxStart = System.nanoTime();
        Lock lock = vertexLocks.get(node);
        lock.lock();
        if (newDistance < tentative[node]) {
            int toBucketIndex = getBucketIndex(newDistance);

            long start = System.nanoTime();
            try {
                buckets.computeIfPresent(getBucketIndex(tentative[node]), (k, bucket) -> {
                    bucket.remove(node);
                    return bucket;
                });

                buckets.computeIfAbsent(toBucketIndex, k -> new ConcurrentSkipListSet<>()).add(node);
                tentative[node] = newDistance;
                parentOnPath[node] = parent;
                maxBucketIndex = Math.max(maxBucketIndex, toBucketIndex);
            } finally {
                // lock.unlock();
            }
            long end = System.nanoTime();

            timtToSynch += end - start;
        }
        lock.unlock();
        long realxEnd = System.nanoTime();
        relaxTime += realxEnd - relaxStart;
    }

    private Double computeMedianEdgeWeight() {
        List<Double> weights = new ArrayList<>();
        for (Edge edge : graph.edges()) {
            weights.add(edge.weight());
        }
        Collections.sort(weights);
        double medianEdgeWeight = weights.get(weights.size() / 2);
        return medianEdgeWeight;
    }

    private Double computeAverageEdgeWeight() {
        double totalWeight = 0.0;
        long edgeCount = graph.numEdges();

        for (Edge edge : graph.edges()) {
            totalWeight += edge.weight();
        }

        return totalWeight / edgeCount;
    }

    private void initialiseLightAndHeavyEdges() throws InvalidAlgorithmParameterException {
        var vertices = graph.vertices();
        ForkJoinPool pool = new ForkJoinPool();
        EdgeInitializationTask task = new EdgeInitializationTask(vertices, graph,
                delta,
                lightEdges, heavyEdges, 0, vertices.length, vertices.length / threadsNum +
                        1);
        pool.invoke(task);

        if (!task.validInput())
            throw new InvalidAlgorithmParameterException("Edge weight cannot be negative");
    }
}

class ParallelTask implements Runnable {
    int[] requestNodes;
    DeltaSteppingAlgorithm parentAlgorithm;
    boolean isLight;
    CountDownLatch latch;

    public ParallelTask(int[] requestNodes, DeltaSteppingAlgorithm parentAlgorithm, boolean isLight,
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

    public void setVertex(int vertex) {
        this.vertex = vertex;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

}
