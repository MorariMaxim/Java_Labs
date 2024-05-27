
package com.example;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class CallableExceptionHandlingExample {
    private static final int NUM_THREADS = 5;
    private static final int INCREMENTS_PER_THREAD = 1000;

    private final int[] counter = { 0 };

    public static void main(String[] args) throws Exception {
        CallableExceptionHandlingExample example = new CallableExceptionHandlingExample();
        example.runExample();
    }

    public void runExample() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            futures.add(executor.submit(new IncrementTask(i)));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        int totalIncrements = 0;
        for (Future<Integer> future : futures) {
            try {
                totalIncrements += future.get();
            } catch (Exception e) {
                System.err.println("Task failed: " + e.getMessage());
            }
        }

        System.out.println("Final counter value: " + counter[0]);
        System.out.println("Total increments: " + totalIncrements);
    }

    private class IncrementTask implements Callable<Integer> {
        private final int threadId;

        public IncrementTask(int threadId) {
            this.threadId = threadId;
        }

        @Override
        public Integer call() throws Exception {
            if (threadId == 2) { // Simulate an exception in one of the threads
                throw new Exception("Simulated exception in thread " + threadId);
            }

            int localIncrements = 0;
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                incrementCounter();
                localIncrements++;
            }
            return localIncrements;
        }

        private synchronized void incrementCounter() {
            counter[0]++;
        }
    }
}
