package com.example;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedArrayExample {
    private static final int ARRAY_SIZE = 10;
    private static final int NUM_THREADS = 5;
    private static final int INCREMENTS_PER_THREAD = 1000;

    private final int[] array = new int[ARRAY_SIZE];
    private long longValue = 0;
    private final AtomicLong atomicLongValue = new AtomicLong(0);
    private final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        SynchronizedArrayExample example = new SynchronizedArrayExample();
        example.runExample2();
    }

    public void runExample() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        // Record the start time
        long startTime = System.nanoTime();

        // Create and start threads
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(new IncrementTask());
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        // Record the end time
        long endTime = System.nanoTime();

        // Calculate the elapsed time
        long duration = endTime - startTime;
        System.out.println("Time taken by runExample: " + duration + " nanoseconds");

        // Print the final state of the array
        for (int i = 0; i < ARRAY_SIZE; i++) {
            System.out.println("array[" + i + "] = " + array[i]);
        }
    }

    private class IncrementTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                incrementArray();
            }
        }

        private void incrementArray() {
            lock.lock();
            try {
                for (int j = 0; j < ARRAY_SIZE; j++) {
                    array[j]++;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void runExample2() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        // Record the start time
        long startTime = System.nanoTime();

        // Create and start threads
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(new IncrementTask2());
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        // Record the end time
        long endTime = System.nanoTime();

        // Calculate the elapsed time
        long duration = endTime - startTime;
        System.out.println("Time taken by runExample2: " + duration + " nanoseconds");

        System.out.println("LongValue: " + longValue);
        System.out.println("atomicLongValue: " + atomicLongValue);
    }

    private class IncrementTask2 implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                // lock.lock();
                try {
                    longValue += 1;
                    atomicLongValue.incrementAndGet(); // Atomic increment
                } finally {
                    // lock.unlock();
                }
            }
        }
    }
}
