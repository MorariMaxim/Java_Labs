
package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FullScreenAlertExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FullScreenAlertExample::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Full Screen Alert Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JButton alertButton = new JButton("Show Full Screen Alert");
        alertButton.addActionListener((ActionEvent e) -> {
            displayFullScreenAlert(frame, "This is a full-screen alert!", "Alert Title");
        });

        frame.add(alertButton, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);   
        frame.setVisible(true);
    }

    private static void displayFullScreenAlert(Component parent, String message, String title) {
        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setUndecorated(true); // Remove title bar and borders
        dialog.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        if (parent != null) {
            dialog.setSize(parent.getSize());
            dialog.setLocation(parent.getLocationOnScreen());
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            dialog.setSize(screenSize);
            dialog.setLocation(0, 0);
        }

        dialog.setTitle(title);
        dialog.setVisible(true);
    }
}
