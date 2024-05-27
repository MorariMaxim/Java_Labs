package com.finalVersion;

import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;

import org.graph4j.Graph; 

import com.ParallelSp.GRParse;

public class TimeAnalysis {
    private static long timeToSolve(Graph graph) throws InterruptedException, InvalidAlgorithmParameterException {
        DeltaSteppingAlgorithm instance = new DeltaSteppingAlgorithm(graph, 0, 3);

        long start = System.nanoTime();
        instance.solve();
        long end = System.nanoTime();
        return end - start;

    }

    public static void multipleTestsOnGraph(int times, Graph graph) {

        long total = 0;
        int count = times;

        for (int i = 0; i < times; i++) {

            try {
                var duration = timeToSolve(graph) / 1_000_000;
                total += duration;
                System.out.println(String.format("Test no %d took %d ms to execute", i, duration));
            } catch (Exception e) {
                System.out.println(String.format("Test no %d failed", i));
                count -= 1;
            }

        }

        System.out.println(
                String.format("%d test successfully executed with an average of %d per test", count, total / count));
    }

    public static void main(String[] args) {

        multipleTestsOnGraph(100, GRParse.parseGRFile("rome.gr"));
    }
}
