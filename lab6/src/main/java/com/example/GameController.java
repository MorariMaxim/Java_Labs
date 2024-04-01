package com.example;

import javafx.util.Pair;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.graph.DefaultEdge;

public class GameController {

    boolean turn = true;

    boolean firstMove = true;

    boolean aiPlayer = false;
    boolean aiTurn = true;

    boolean firstMoveNotAI;

    MainFrame mainFrame;
    GameState gameState;

    int lastMove;

    Pair<Set<Pair<Integer, Integer>>, Set<Integer>> maximalMatching = null;

    public GameController(MainFrame mainFrame, int rows, int cols, int numOfEdges) {
        this.mainFrame = mainFrame;
        this.gameState = new GameState(rows, cols, numOfEdges);
    }

    void onNodeClick(int row, int col) {

        if (aiPlayer && firstMoveNotAI) {
            mainFrame.canvas.endGameAllert("Supported only if the ai makes the first move", "Error");
            return;

        }

        var front = gameState.computePlayerFront(boolNodeState(!turn));

        // System.out.println(front);

        if (aiPlayer && aiTurn) {

            var row_col = gameState.uniqueIdToRowCol(getAiMove());

            row = row_col[0];

            col = row_col[1];

        }

        if (front.isEmpty() && !firstMove) {
            System.out.println(boolToPlayerTurn(turn) + " lost");
            mainFrame.canvas.endGameAllert(boolToPlayerTurn(turn) + " lost", "EndGame");
        }

        boolean allow = false;

        if (firstMove && gameState.gridStones[row][col] == NodeState.UNOCCUPIED) {
            allow = true;
        }
        if (front.contains(new NodeXY(row, col)) || allow) {

            gameState.setGridStone(row, col, boolNodeState(turn));

            mainFrame.canvas.drawStone(row, col, boolToPlayerTurn(turn));

            turn = !turn;

            System.out.println(turn);

            firstMove = false;

            aiTurn = !aiTurn;

            lastMove = gameState.rowColToUniqueId(row, col);
        }

    }

    Set<Integer> matchedVertices = null;
    Set<Integer> unmatchedVertices = null;
    Set<Pair<Integer, Integer>> unexhaustedMatches = null;

    int getAiMove() {

        if (maximalMatching == null)
            maximalMatching = gameState.computeMatching();

        if (matchedVertices == null) {

            unexhaustedMatches = new HashSet<>(maximalMatching.getKey());

            matchedVertices = maximalMatching.getKey().stream()
                    .flatMap((Pair<Integer, Integer> pair) -> Stream.of(pair.getKey(), pair.getValue()))
                    .collect(Collectors.toSet());

            unmatchedVertices = new HashSet<>(maximalMatching.getValue());
            unmatchedVertices.removeAll(matchedVertices);

        }
        System.out.print("Ai chooses ");

        Integer move = -1;
        if (firstMove) {
            System.out.println(move = unmatchedVertices.iterator().next());

            unmatchedVertices.remove(move);
        }

        else {

            var match = new ArrayList<Integer>();
            match.add(-1);

            unexhaustedMatches.stream()
                    .filter(pair -> pair.getKey().equals(lastMove) || pair.getValue().equals(lastMove))
                    .findFirst()
                    .ifPresent(foundPair -> {
                        match.set(0, foundPair.getKey() == lastMove ? foundPair.getValue() : foundPair.getKey());
                        unexhaustedMatches.remove(foundPair);
                    });

            if (match.get(0) == -1)
                throw new RuntimeException("wrong match of the last move");

            System.out.println(move = match.get(0));

            matchedVertices.remove(move);

        }

        return move;
    }

    private PlayerTurn boolToPlayerTurn(boolean turn) {

        return turn ? PlayerTurn.FIRST_PLAYER : PlayerTurn.SECOND_PLAYER;
    }

    private NodeState boolNodeState(boolean turn) {

        return turn ? NodeState.FIRST_PLAYER : NodeState.SECOND_PLAYER;
    }

    Set<GridEdge> getEdges() {

        return gameState.getEdges();
    }

    void saveState(String fileName) {

        try {

            Files.newOutputStream(Paths.get(fileName), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(turn);
            objectOutputStream.writeObject(firstMove);
            objectOutputStream.writeObject(gameState.rows);
            objectOutputStream.writeObject(gameState.cols);
            objectOutputStream.writeObject(gameState.gridStones);
            objectOutputStream.writeObject(gameState.edges);
            System.out.println(gameState.edges);
            objectOutputStream.close();

            fileOutputStream.close();

            System.out.println("State saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    void recoverState(String fileName) {

        try {

            FileInputStream fileOutputStream = new FileInputStream(fileName);

            ObjectInputStream objectOutputStream = new ObjectInputStream(fileOutputStream);

            turn = (boolean) objectOutputStream.readObject();
            firstMove = (boolean) objectOutputStream.readObject();
            gameState.rows = (int) objectOutputStream.readObject();
            gameState.cols = (int) objectOutputStream.readObject();
            gameState.gridStones = (NodeState[][]) objectOutputStream.readObject();
            gameState.edges = (Set<GridEdge>) objectOutputStream.readObject();
            System.out.println(gameState.edges);
            gameState.firstPlayerFront = null;
            gameState.secondPlayerFront = null;

            objectOutputStream.close();

            fileOutputStream.close();

            System.out.println("State recovered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error recovering state: " + e.getMessage());
        }
    }

    int getRows() {
        return gameState.rows;
    }

    int getCols() {
        return gameState.cols;
    }

    NodeState[][] getStones() {
        return gameState.gridStones;
    }

    void playAgainstAi() {

        if (!firstMove) {
            firstMoveNotAI = true;
        }
        aiPlayer = true;
        gameState.computeMatching();

    }
}
