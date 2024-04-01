package com.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class DrawingPanel extends JPanel {
    final MainFrame mainFrame;
    int rows, cols;
    int canvasWidth = 600, canvasHeight = 600;
    int boardWidth, boardHeight;
    int cellWidth, cellHeight;
    int padX, padY;
    int stoneSize = 30;
    BufferedImage image;
    Graphics2D offscreen;
    boolean alternate = false;
    boolean resetGameController = true;
    boolean drawStones = false;
    Set<GridEdge> edges;
    private MouseAdapter mouseAdapter = null;

    boolean repaintGrid = true;

    public DrawingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        createOffscreenImage();
        init(mainFrame.configPanel.getRows(), mainFrame.configPanel.getCols());
    }

    void loadSaved() {
        resetGameController = false;
        drawStones = true;
        createOffscreenImage();
        init(mainFrame.gameController.getRows(), mainFrame.gameController.getCols());
        System.out.println(mainFrame.gameController.getRows() + ", " + mainFrame.gameController.getCols());
        repaint();
    }

    private void createOffscreenImage() {
        image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        offscreen = image.createGraphics();
        offscreen.setColor(Color.WHITE);
        offscreen.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    final void init(int rows, int cols) {
        repaintGrid = true;
        this.rows = rows;
        this.cols = cols;
        this.padX = stoneSize + 30;
        this.padY = stoneSize + 30;
        this.cellWidth = (canvasWidth - 2 * padX) / (cols - 1);
        this.cellHeight = (canvasHeight - 2 * padY) / (rows - 1);
        this.boardWidth = (cols - 1) * cellWidth;
        this.boardHeight = (rows - 1) * cellHeight;
        setPreferredSize(new Dimension(canvasWidth, canvasHeight));

        if (mouseAdapter != null) {
            this.removeMouseListener(mouseAdapter);
        }

        this.addMouseListener(mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                var node = getNodeFromClick(e.getX(), e.getY());

                mainFrame.gameController.onNodeClick(node[0], node[1]);

                repaintGrid = false;
                repaint();
            }
        });

        if (resetGameController)
            mainFrame.resetGameController();

        edges = mainFrame.gameController.getEdges();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

    private void paintGrid() {
        offscreen.setColor(Color.WHITE);
        offscreen.fillRect(0, 0, canvasWidth, canvasHeight);
        offscreen.setColor(Color.DARK_GRAY);
        for (int row = 0; row < rows; row++) {
            int x1 = padX;
            int y1 = padY + row * cellHeight;
            int x2 = padX + boardWidth;
            int y2 = y1;
            offscreen.drawLine(x1, y1, x2, y2);
        }

        for (int col = 0; col < cols; col++) {
            int x1 = padX + col * cellWidth;
            int y1 = padY;
            int x2 = x1;
            int y2 = padY + boardHeight;
            offscreen.drawLine(x1, y1, x2, y2);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = padX + col * cellWidth;
                int y = padY + row * cellHeight;
                offscreen.setColor(Color.LIGHT_GRAY);
                offscreen.drawOval(x - stoneSize / 2, y - stoneSize / 2, stoneSize, stoneSize);
            }
        }

        edges.stream().forEach(edge -> {
            drawThickerLine(edge.row1, edge.col1, edge.row2, edge.col2, offscreen, 5);
        });

    }

    void drawStones() {

        var stones = mainFrame.gameController.getStones();
        for (int row = 0; row < stones.length; row++) {

            for (int col = 0; col < stones[0].length; col++) {

                if (stones[row][col] == NodeState.FIRST_PLAYER) {
                    drawStone(row, col, PlayerTurn.FIRST_PLAYER);
                }
                if (stones[row][col] == NodeState.SECOND_PLAYER) {
                    drawStone(row, col, PlayerTurn.SECOND_PLAYER);
                }
            }
        }

    }

    private void drawThickerLine(int row1, int col1, int row2, int col2, Graphics2D g2d, int thickness) {
        Stroke originalStroke = g2d.getStroke();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(thickness));

        g2d.drawLine(colToX(col1), rowToY(row1), colToX(col2), rowToY(row2));

        g2d.setStroke(originalStroke);
    }

    private int rowToY(int row) {

        return row * cellHeight + padX;
    }

    private int colToX(int col) {

        return col * cellWidth + padY;
    }

    private int[] getNodeFromClick(int x, int y) {

        int col = (x - padX + cellWidth / 2) / cellWidth;
        int row = (y - padY + cellHeight / 2) / cellHeight;

        return new int[] { row, col };
    }

    void drawStone(int row, int col, PlayerTurn turn) {

        offscreen.setColor(turn == PlayerTurn.FIRST_PLAYER ? Color.RED : Color.blue);

        int x = padX + col * cellWidth;
        int y = padY + row * cellHeight;

        offscreen.fillOval(x - stoneSize / 2, y - stoneSize / 2, stoneSize, stoneSize);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (repaintGrid)
            paintGrid();
        if (drawStones)
            drawStones();
        super.paint(g);
    }

    void endGameAllert(String message, String title) {
        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setSize(this.getSize());
        dialog.setLocation(this.getLocationOnScreen());

        dialog.setTitle(title);
        dialog.setVisible(true);
    }
 

    private static void exportImage(BufferedImage image, String filePath) {
        try {
            File file = new File(filePath);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void saveImage(){

        exportImage(image, "image.png");
    }
}
