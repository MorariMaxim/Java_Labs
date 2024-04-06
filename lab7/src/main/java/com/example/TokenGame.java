package com.example;

public class TokenGame {
    public static void main(String[] args) {

        int n = 50;
        int timeLimit = 10000;

        TokenBag tokenBag = new TokenBag(n);
        Timekeeper timekeeper = new Timekeeper(timeLimit, Thread.currentThread(), 10);
        timekeeper.start();

        Player[] players = {
                new Player("Player 1", tokenBag) 
        };

        for (Player player : players) {
            player.start();
        }
        try {

            while (tokenBag.getRemaining() != 0) {
                for (Player player : players) {

                    synchronized (tokenBag) {
                        player.setMyTurn(true);
                        tokenBag.notifyAll();

                    }

                    Thread.sleep(1);
                }

            }
            timekeeper.interrupt();

        } catch (InterruptedException e) {
            System.out.println("Time is out!");
        }

        Player winner = players[0];    
        for (Player player : players) {
            if (player.getMaxSequenceValue() > winner.getMaxSequenceValue()) {
                winner = player;
            }
            player.interrupt();
        }

        System.out.println("Game Over!");
        System.out.println("Winner: " + winner.getName() + " with sequence value: " + winner.getMaxSequenceValue() + "\n" + winner.getSequenceString());

    }
}
