package com.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageExporter {

    public static void main(String[] args) {
        BufferedImage image = createSampleImage();

        // Specify the file path where you want to save the image
        String filePath = "image.png";

        // Export the image as PNG
        exportImage(image, filePath);

        System.out.println("Image exported successfully to: " + filePath);
    }

    private static BufferedImage createSampleImage() {
        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Draw something on the image (for demonstration)
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, width, height);

        // Dispose of the graphics context
        g2d.dispose();

        return image;
    }

    private static void exportImage(BufferedImage image, String filePath) {
        try {
            File file = new File(filePath);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
