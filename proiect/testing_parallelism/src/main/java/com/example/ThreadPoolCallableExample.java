package com.example;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

public class ThreadPoolCallableExample {
    private static final int ARRAY_SIZE = 10;
    private static final int NUM_THREADS = 5;
    private static final int INCREMENTS_PER_THREAD = 1000;

    private final int[] array = new int[ARRAY_SIZE];
    private final AtomicLong longValue = new AtomicLong(0);

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolCallableExample example = new ThreadPoolCallableExample();
        example.runExample();
    }

    public void runExample() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS - 3; i++) {
            futures.add(executor.submit(new IncrementTask()));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Print the final state of the array
        for (int i = 0; i < ARRAY_SIZE; i++) {
            System.out.println("array[" + i + "] = " + array[i]);
        }

        // Print the final value of longValue
        System.out.println("LongValue: " + longValue.get());

        // Retrieve and print the results from the futures
        int totalIncrements = 0;
        for (Future<Integer> future : futures) {
            try {
                totalIncrements += future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total Increments: " + totalIncrements);
    }

    private class IncrementTask implements Callable<Integer> {
        @Override
        public Integer call() {
            int localIncrements = 0;
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                incrementArray();
                incrementLongValue();
                localIncrements++;
            }
            return localIncrements;
        }

        private void incrementArray() {
            synchronized (array) {
                for (int j = 0; j < ARRAY_SIZE; j++) {
                    array[j]++;
                }
            }
        }

        private void incrementLongValue() {
            longValue.incrementAndGet();
        }
    }
}
