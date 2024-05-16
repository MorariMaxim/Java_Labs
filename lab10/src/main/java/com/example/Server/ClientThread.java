package com.example.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.example.Game.Player;

import com.example.Game.BattleshipGame;

public class ClientThread extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BattleshipGame game;
    private GameServer gameServer;

    private Player player = null;

    public ClientThread(Socket socket, GameServer server) {
        this.clientSocket = socket;
        this.gameServer = server;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server received the request: " + inputLine);

                String[] tokens = inputLine.split("\\s+");
                String commandName = tokens[0]; 

                String response = "Unrecognised command: " + commandName;

                if (!commandName.equals("id") && player == null)
                    response = "first tell your name";

                if (commandName.equals("id")) {
                    if (tokens.length < 2) {
                        response = "missing name parameter";
                    } else {
                        player = new Player(tokens[1]);
                        response = "Wellcome, " + tokens[1];
                    }
                }

                if (commandName.equals("createGame")) {

                    if (tokens.length < 5) {
                        response = "provide game name, boardSize and shipUnits and blitzTimer";
                    } else {

                        String gameName = tokens[1];
                        int size = Integer.parseInt(tokens[2]);
                        int units = Integer.parseInt(tokens[3]);
                        int timer = Integer.parseInt(tokens[4]);
                        System.out.println("size, units = " + size + " " + units);

                        boolean created = gameServer.createGame(gameName, size,
                                units, timer);

                        if (!created)
                            response = "game already exists";
                        else
                            response = "game successfully created";

                        game = gameServer.getGame(gameName);

                        game.connectPlayer(player);
                    }

                } else if (commandName.equals("joinGame")) {

                    if (tokens.length < 2)
                        response = "provide game name";

                    else {
                        game = gameServer.getGame(tokens[1]);
                        game.connectPlayer(player);
                        response = "successfully joined game " + tokens[1];
                    }

                } else if (commandName.equals("attack")) {
                    if (game == null) {
                        response = "you have to join a game";
                    }

                    else if (tokens.length < 3) {
                        response = "missing x and y coordinates";
                    } else {
                        response = game.attack(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), player);
                    }
                } else if (commandName.equals("startGame")) {
                    if (game == null) {
                        response = "you have to join a game";
                    } else
                        response = game.startGame();
                } else if (commandName.equals("board")) {
                    if (game == null) {
                        response = "you have to join a game";
                    } else {
                        response = stringFromMatrix(game.getBoard(player));
                    }

                }

                out.println(response + "\n");
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String stringFromMatrix(int[][] matrix) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                stringBuilder.append(matrix[i][j] + " ");
            }
            if (i != matrix.length - 1)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
