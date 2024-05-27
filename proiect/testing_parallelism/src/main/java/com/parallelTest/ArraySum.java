package com.parallelTest;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class ArraySum {

    static class SumTask implements Callable<Long> {
        private final int[] array;
        private final int start;
        private final int end;

        SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public Long call() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }
    }

    public static long parallelSum(int[] array, int numThreads) throws InterruptedException, ExecutionException {
        int length = array.length;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        int chunkSize = (length + numThreads - 1) / numThreads; // Calculate chunk size

        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(i + chunkSize, length);
            futures.add(executor.submit(new SumTask(array, i, end)));
        }

        long totalSum = 0;
        for (Future<Long> future : futures) {
            totalSum += future.get(); // Retrieve and add the results
        }

        executor.shutdown();
        return totalSum;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        int matrixSize = 5_000_00000;
        int[] matrix = generateMatrix(matrixSize);
 
        int numThreads = 2; // Number of threads in the pool
        int numTests = 10; // Number of tests to run
        long totalTime = 0;

        for (int i = 0; i < numTests; i++) {
            long start = System.nanoTime();
            long sum = parallelSum(matrix, numThreads);
            long end = System.nanoTime();
            long duration = (end - start) / 1000000; // Convert to milliseconds
            totalTime += duration;

            System.out.println("Test " + (i + 1) + ": Sum = " + sum + ", Time = " + duration + " ms");
        }

        double averageTime = totalTime / (double) numTests;
        System.out.println("Average time: " + averageTime + " ms");

    }

    private static int[] generateMatrix(int size) {
        int[] matrix = new int[size];
        for (int i = 0; i < size; i++) {
            matrix[i] = i + 1; // Initialize matrix with sequential values for simplicity
        }
        return matrix;
    }
}
