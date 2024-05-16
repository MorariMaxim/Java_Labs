package com.example.Game;

public class BattleshipGame {
    int boardSize;
    int units;
    int totalTime;// ms

    private Player player1 = null;
    private Player player2 = null;
    private boolean turn = false; // 0 for player1
    private boolean winner;
    private GameBoard board1;
    private GameBoard board2;
    private boolean gameOver = false;

    private BlitzTimer timer1;
    private BlitzTimer timer2;
    private Boolean[] timeOut1 = { false };
    private Boolean[] timeOut2 = { false };
    private boolean gameStarted = false;

    public BattleshipGame(int size, int units, int timer) {
        this.boardSize = size;
        this.units = units;
        this.totalTime = timer;

        board1 = new GameBoard(boardSize, units);
        board2 = new GameBoard(boardSize, units);
    }

    public void placeShipsRandomly() {
    }

    public String attack(int x, int y, Player player) {

        Player currentPlayer = turn ? player2 : player1;
        BlitzTimer currenTimer = turn ? timer2 : timer1;

        if (gameOver)
            return "Game over, " + (winner ? player2 : player1).getName() + " won";

        if (currentPlayer != player)
            return "not your turn!";

        if (!gameStarted)
            return "Game didn't start";

        if (Boolean.TRUE.equals(timeOut1[0]))
            return "Time out, Player1 lost";

        if (Boolean.TRUE.equals(timeOut2[0]))
            return "Time out, Player2 lost";

        GameBoard currentAttackedGameBoard = turn ? board1 : board2;

        if (currentAttackedGameBoard.receiveAttack(x, y)) {
            if (currentAttackedGameBoard.allShipsSunk()) {
                gameOver = true;
                winner = turn;
            }
            return "Hit " + currenTimer.getTimeRemaining() + " time left";
        }

        if (turn) {
            timer1.run();
            timer2.stop();
        } else {
            timer2.run();
            timer1.stop();
        }

        turn = !turn;

        return "Miss, " + currenTimer.getTimeRemaining() + " time left";

    }

    public String startGame() {
        if (gameStarted)
            return "Game already started";

        if (player1 == null)
            return "Need 2 more players";

        if (player2 == null)
            return "Need 1 more player";

        timer1 = new BlitzTimer(totalTime, timeOut1);
        timer2 = new BlitzTimer(totalTime, timeOut2);

        timer1.run();

        gameStarted = true;

        return "Game started, waiting for " + player1.getName() + "'s move";
    }

    public boolean connectPlayer(Player joningPlayer) {
        if (player1 == null) {
            player1 = joningPlayer;
        } else if (player2 == null) {
            player2 = joningPlayer;

        } else
            return false;
        return true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner ? player2 : player1;
    }

    public int[][] getBoard(Player player) {
        if (player == player1)
            return board1.getGrid();
        if (player == player2)
            return board2.getGrid();
        return null;
    }
}
