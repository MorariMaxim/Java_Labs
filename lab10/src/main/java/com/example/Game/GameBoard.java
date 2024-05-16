package com.example.Game;

import java.util.Random;

class GameBoard {
    private int[][] grid;

    public int[][] getGrid() {
        return grid;
    }

    private int size;
    private int units;
    int remainingUnits;

    public GameBoard(int size, int units) {
        this.size = size;
        grid = new int[size][size];
        this.units = units;
        this.remainingUnits = units;
        fillGrid();
    }

    void fillGrid() {
        int counter = 0;
        Random random = new Random();

        while (counter != units) {
            System.out.println("size = " + size);
            int x = random.nextInt(size);
            int y = random.nextInt(size);

            if (grid[x][y] == 0) {
                grid[x][y] = 1;
                counter++;
            }

        }

    }

    public int getSize() {
        return size;
    }

    public void placeShip(int x, int y) {
        grid[x][y] = 1; 
    }

    public boolean receiveAttack(int x, int y) {

        System.out.println("attack on x,y= " + x + ", " + y + ", grid[x][y]=" + grid[x][y]);
        if (grid[x][y] == 1) {
            grid[x][y] = -1; 
            remainingUnits -= 1;
            return true; 
        }
        return false; 
    }

    public boolean allShipsSunk() {
        return remainingUnits == 0;
    }

}