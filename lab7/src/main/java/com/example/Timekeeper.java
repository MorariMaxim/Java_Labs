package com.example;

public class Timekeeper extends Thread {
    private int timeLimit;
    int intervals = 1;
    int sleptIntervals = 0;
    Thread main;

    public Timekeeper(int timeLimit, Thread mainThread, int intervals) {
        this.timeLimit = timeLimit;
        this.main = mainThread;
        this.intervals = intervals;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(timeLimit / intervals);
                System.out.println(
                        sleptIntervals * timeLimit / intervals + " miliseconds elapsed from the beginning of the game");

                sleptIntervals++;

                if (sleptIntervals == intervals)
                    break;
            } catch (InterruptedException e) {
                return;
            }
        }

        main.interrupt();
    }
}