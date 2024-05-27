package com.parallelTest;

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MatrixSum {

    static class SumTask extends RecursiveTask<Long> {
        private final int[] matrix;
        private final int start;
        private final int end;
        private final int threshold;

        SumTask(int[] matrix, int start, int end, int threshold) {
            this.matrix = matrix;
            this.start = start;
            this.end = end;
            this.threshold = threshold;
        }

        @Override
        protected Long compute() {
            if (end - start <= threshold) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += matrix[i];
                }
                return sum;
            } else {
                int mid = start + (end - start) / 2;
                SumTask leftTask = new SumTask(matrix, start, mid, threshold);
                SumTask rightTask = new SumTask(matrix, mid, end, threshold);
                leftTask.fork();
                long rightSum = rightTask.compute();
                long leftSum = leftTask.join();
                return leftSum + rightSum;
            }
        }
    }

    public static void main(String[] args) {

        long initstart = System.nanoTime();
        int matrixSize = 5_000_00000;
        int threadsNum = 3;
        int threshold = matrixSize / threadsNum + 1;

        int[] matrix = generateMatrix(matrixSize);
        long initend = System.nanoTime();
        System.out.println("initt took: " + (initend - initstart) / 1000000 + " ms ");

        ForkJoinPool pool = new ForkJoinPool();

        int testsNum = 10;
        long totalTime = 0;

        for (int i = 0; i < testsNum; i++) {
            long start = System.nanoTime();
            long result = pool.invoke(new SumTask(matrix, 0, matrix.length, threshold));

            // System.out.println("Sum of the matrix: " + result);

            long end = System.nanoTime();

            totalTime += end - start;

            // System.out.println("It took: " + (end - start) / 1000000 + " ms ");
        }

        System.out.println("Average: " + totalTime / testsNum / 1000000 + " ms ");

    }

    private static int[] generateMatrix(int size) {
        int[] matrix = new int[size];
        for (int i = 0; i < size; i++) {
            matrix[i] = i + 1; // Initialize matrix with sequential values for simplicity
        }
        return matrix;
    }
}
