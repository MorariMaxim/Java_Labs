package com.example.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import com.example.Game.BattleshipGame;
import com.example.Game.BlitzTimer;

public class GameServer {
    private int port;
    private Hashtable<String, BattleshipGame> games = new Hashtable<>();
    private boolean running = true;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port);) {

            System.out.println("Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, this);
                clientThread.start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        int port = 1234; 
        GameServer server = new GameServer(port);
        server.start();

    }

    boolean createGame(String name, int size, int units, int timer) {

        if (games.containsKey(name))

            return false;

        games.put(name, new BattleshipGame(size,units, timer));

        return true;

    }

    BattleshipGame getGame(String name) {
        return games.get(name);
    }

    
}
