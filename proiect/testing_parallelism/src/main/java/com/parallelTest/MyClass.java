package com.parallelTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import org.graph4j.Edge;
import org.graph4j.Graph;

import com.ParallelSp.EdgeInitializationTask;
import com.ParallelSp.GRParse;
import com.parallelTest.MatrixSum.SumTask;

public class MyClass {
    private final Graph graph;
    private final double DELTA;
    private final Map<Integer, List<Edge>> lightEdges;
    private final Map<Integer, List<Edge>> heavyEdges;
    private final int threadsNum;

    public MyClass(Graph graph, double DELTA, int threadsNum) {
        this.graph = graph;
        this.DELTA = DELTA;
        this.threadsNum = threadsNum;
        this.lightEdges = new ConcurrentHashMap<>();
        this.heavyEdges = new ConcurrentHashMap<>();
    }

    public void initialiseLightAndHeavyEdges() {

        var vertices = graph.vertices();
        ForkJoinPool pool = new ForkJoinPool();
        EdgeInitializationTask task = new EdgeInitializationTask(vertices, graph, DELTA,
                lightEdges, heavyEdges, 0, vertices.length, vertices.length / threadsNum + 1);
        pool.invoke(task);
    }

    public static void main(String[] args) {

        Graph graph = GRParse.parseGRFile("ny.gr");

        int testsNum = 10;
        long totalTime = 0;
        int threadsNum = 10;

        for (int i = 0; i < testsNum; i++) {
            long start = System.nanoTime();

            MyClass myClass = new MyClass(graph, 3.0, threadsNum);

            myClass.initialiseLightAndHeavyEdges();

            long end = System.nanoTime();

            totalTime += end - start;
        }

        System.out.println("Average init time: " + totalTime / testsNum / 1000000 + " ms ");
    }
}
