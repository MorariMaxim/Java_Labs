package com.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ConfigPanel extends JPanel {
    final MainFrame frame;
    JLabel label;
    JSpinner spinnerRows;
    JSpinner spinnerCols;
    int rows = 5;
    int cols = 5;

    JButton createButton;

    public ConfigPanel(MainFrame frame) {
        this.frame = frame;
        init();
    }

    private void init() { 
        label = new JLabel("Grid size:");
        spinnerRows = new JSpinner(new SpinnerNumberModel(rows, 2, 100, 1));
        spinnerCols = new JSpinner(new SpinnerNumberModel(cols, 2, 100, 1));
 
        createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows = (int) spinnerRows.getValue();
                int cols = (int) spinnerCols.getValue(); 
                frame.canvas.resetGameController = true;
                frame.canvas.init(rows, cols);
                frame.canvas.repaint();
            }
        });
 
        add(label);
        add(new JLabel("Rows:"));
        add(spinnerRows);
        add(new JLabel("Cols:"));
        add(spinnerCols);
        add(createButton);
    }

    public int getRows() {
        return rows = (int) spinnerRows.getValue();
    }

    public int getCols() {
        return cols = (int) spinnerCols.getValue();
    }
}