package com.Bonus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTester {
    private static final String SERVICE_URL = "http://localhost:8081/books";
    private static final int NUM_THREADS = 100;
    private static final long TEST_DURATION_SECONDS = 20;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    private static final AtomicInteger successfulRequests = new AtomicInteger(0);

    public static void main(String[] args) {
        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        sendRequest();
                    } catch (IOException e) {
                        System.err.println("failed sendReuqest, " + e.getMessage());
                    }
                }
            });
        }

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(TEST_DURATION_SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long testEndTime = System.currentTimeMillis();
        long testDurationSeconds = (testEndTime - testStartTime) / 1000;
        double requestsPerMinute = (double) successfulRequests.get() / testDurationSeconds * 60;

        System.out.println("Requests per minute: " + requestsPerMinute);
    }

    private static void sendRequest() throws IOException {
        URL url = new URL(SERVICE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            successfulRequests.incrementAndGet();
        }

        connection.disconnect();
    }
}
