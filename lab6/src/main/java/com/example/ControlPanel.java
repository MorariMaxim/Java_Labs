package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class ControlPanel extends JPanel {
    final MainFrame frame;
    JButton loadBtn;
    JButton saveBtn;
    JButton exitBtn;
    JButton saveImageBtn;
    JButton showMatchingButton;

    public ControlPanel(MainFrame frame) {
        this.frame = frame;
        init();
    }

    private void init() {
        loadBtn = new JButton("Load Game");
        saveBtn = new JButton("Save Game");
        exitBtn = new JButton("Exit");
        saveImageBtn = new JButton("Save Image");
        showMatchingButton = new JButton("Play agaist Ai");
        ;

        loadBtn.addActionListener(this::loadGame);
        saveBtn.addActionListener(this::saveGame);
        exitBtn.addActionListener(this::exitGame);
        saveImageBtn.addActionListener(this::saveImage);
        showMatchingButton.addActionListener(this::playAgainstAi);

        setLayout(new GridLayout(1, 3));

        add(loadBtn);
        add(saveBtn);
        add(exitBtn);
        add(saveImageBtn);
        add(showMatchingButton);
    }

    void playAgainstAi(ActionEvent e) {

        frame.gameController.playAgainstAi();

    }

    private void loadGame(ActionEvent e) {

        System.out.println("Load game");

        frame.gameController.recoverState("./saved_state.bin");

        frame.canvas.loadSaved();
    }

    private void saveGame(ActionEvent e) {

        System.out.println("Save game");

        frame.gameController.saveState("./saved_state.bin");
    }

    private void exitGame(ActionEvent e) {
        frame.dispose();
    }

    private void saveImage(ActionEvent e) {
        System.out.println("Save imag");

        frame.canvas.saveImage();
    }
}