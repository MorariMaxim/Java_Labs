package com.parallelTest;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import org.graph4j.Edge;
import org.graph4j.Graph;

import com.ParallelSp.GRParse;

import java.util.concurrent.ForkJoinPool;

public class GraphAverageWeightCalculator {

    private final Graph graph;

    public GraphAverageWeightCalculator(Graph graph) {
        this.graph = graph;
    }

    private static class TotalWeightTask extends RecursiveTask<Double> {
        private final Edge[] edges;
        private final int start;
        private final int end;
        private final int threshold; // Adjust the threshold as needed

        public TotalWeightTask(Edge[] edges, int start, int end, int threshold) {
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
