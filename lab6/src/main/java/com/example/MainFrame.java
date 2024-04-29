package com.example;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    ConfigPanel configPanel;
    ControlPanel controlPanel;
    DrawingPanel canvas;
    GameController gameController;

    public MainFrame() {
        super("My Drawing Application");
        init();
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // create the components, exactly in this order
        configPanel = new ConfigPanel(this);
        gameController = new GameController(this, configPanel.getRows(), configPanel.getCols(),
                GameState.getMaxNumberOfEdge(configPanel.getRows(), configPanel.getCols()) * 2 );
        canvas = new DrawingPanel(this);
        controlPanel = new ControlPanel(this);
 
        add(canvas, BorderLayout.CENTER); 
        add(configPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);        
 
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    void resetGameController() {
        gameController = new GameController(this, configPanel.getRows(), configPanel.getCols(),
                GameState.getMaxNumberOfEdge(configPanel.getRows(), configPanel.getCols()) *5/6);
    }
}
