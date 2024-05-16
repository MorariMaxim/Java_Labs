package com.example.Game;

import java.util.Timer;
import java.util.TimerTask;

public class BlitzTimer {
    private Timer timer;
    private long timeRemaining;
    public long getTimeRemaining() {
        return timeRemaining;
    }

    private boolean isRunning;private Boolean[] timeOut;


    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    int period = 100;// ms

    public BlitzTimer(long totalTime, Boolean[] timeOut) {
        this.timeRemaining = totalTime;
        this.isRunning = false;
        this.timeOut = timeOut;
        this.timeOut[0] = false;
    }

    public void run() {
        isRunning = true;

        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {

                    if (timeRemaining < 0) {
                        timeOut[0] = true;
                        timer.cancel();
                        System.out.println("timeout");
                    }

                    if (isRunning) {
                        timeRemaining -= period;
                    }


                }
            };
            timer.scheduleAtFixedRate(task, 0, period); 
        }
    }

    public void stop() {
        isRunning = false;
    }
}
