package com.ParallelSp;

import java.security.InvalidAlgorithmParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import org.graph4j.Edge;
import org.graph4j.Graph;

public class EdgeInitializationTask extends RecursiveAction {
    private final int threshold; // Adjust as needed
    private final int[] vertices;
    private final Graph graph;
    private final double DELTA;
    private final Map<Integer, List<Edge>> lightEdges;
    private final Map<Integer, List<Edge>> heavyEdges;
    private final int start;
    private final int end;
    private Exception exception;

    boolean validInput() {
        return exception == null;
    }

    public EdgeInitializationTask(int[] vertices, Graph graph, double DELTA,
            Map<Integer, List<Edge>> lightEdges, Map<Integer, List<Edge>> heavyEdges,
            int start, int end, int threshold) {
        this.vertices = vertices;
        this.graph = graph;
        this.DELTA = DELTA;
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
                EdgeInitializationTask leftTask = new EdgeInitializationTask(vertices, graph, DELTA,
                        lightEdges, heavyEdges, start, mid, threshold);
                EdgeInitializationTask rightTask = new EdgeInitializationTask(vertices, graph, DELTA,
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
            for (Edge edge : graph.edgesOf(vertex)) {
                if (edge.weight() < 0.0)
                    throw new InvalidAlgorithmParameterException("Edge weight cannot be negative");

                Map<Integer, List<Edge>> targetMap = (edge.weight() <= DELTA) ? lightEdges : heavyEdges;
                targetMap.computeIfAbsent(edge.source(), k -> new LinkedList<>()).add(edge);
            }
        }
    }
}
