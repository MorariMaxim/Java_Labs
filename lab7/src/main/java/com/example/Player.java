package com.example;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

public class Player extends Thread {
    private String name;
    private List<Token> tokens;
    private TokenBag tokenBag;
    private boolean myTurn;
    boolean gameOver = false;

    List<Integer> sequence = null;

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Player(String name, TokenBag tokenBag) {
        this.name = name;
        this.tokenBag = tokenBag;
        this.tokens = new ArrayList<>();
        this.myTurn = false;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (tokenBag) {
                while (!myTurn) {
                    try {
                        tokenBag.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                Token token = tokenBag.extractToken();
                if (token != null) {
                    tokens.add(token);
                }

                if (token == null) {
                    myTurn = false;
                    tokenBag.notifyAll();
                    break;
                }
                myTurn = false;
            }
        }
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    private void getSequence() {
        try {
            Map<Integer, Integer> originalToNew = new HashMap<>();
            Map<Integer, Integer> newToOriginal = new HashMap<>();
            int counter = 0;

            for (var token : tokens) {
                if (!originalToNew.containsKey(token.getNumber1())) {
                    originalToNew.put(token.getNumber1(), counter);
                    if ((token.getNumber1() == -1) || (counter == -1))
                        System.out.println("-1");
                    counter++;
                }
                if (!originalToNew.containsKey(token.getNumber2())) {
                    originalToNew.put(token.getNumber2(), counter);
                    if ((token.getNumber2() == -1) || (counter == -1))
                        System.out.println("-1");
                    counter++;
                }

                newToOriginal.put(originalToNew.get(token.getNumber1()), token.getNumber1());
                newToOriginal.put(originalToNew.get(token.getNumber2()), token.getNumber2());
            }
            var graphbuilder = GraphBuilder.numVertices(counter);

            for (var token : tokens) {

                if ((originalToNew.get(token.getNumber1()) == null)
                        || (originalToNew.get(token.getNumber2()) == null)) {
                    System.out.println("problem");
                }
            }

            for (var token : tokens) {
                int source = originalToNew.get(token.getNumber1());
                int target = originalToNew.get(token.getNumber2());

                 
                graphbuilder.addEdge(source, target);

                 
                 

            }

            Graph graph = graphbuilder.buildGraph();

            PalmerAlgorithm instance = new PalmerAlgorithm(graph);

            System.out.println(name + " used Palmer'a algorithm");

            sequence = instance.circle;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            TokenGraph tokenGraph = new TokenGraph(tokens);

            sequence = tokenGraph.getCycle();

            System.out.println(name + " used a simple heuristic");
        }

    }

    public int getMaxSequenceValue() {
        if (sequence == null)
            getSequence();
        return sequence.size();
    }

    public String getSequenceString() {
        if (sequence == null)
            getSequence();
        StringBuilder representation = new StringBuilder();

        for (int i = 0; i < sequence.size(); ++i) {

            representation.append("(" + sequence.get(i) + ", " + sequence.get((i + 1) % sequence.size()) + ") ");

        }

        return representation.toString();
    }
}