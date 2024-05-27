package com.example;

import java.io.*;
import java.util.*;

public class SortByLastColumn {
    public static void main(String[] args) {
        // Hardcoded file path
        String filePath = "C:\\Users\\user\\Downloads\\note.txt";
        List<String[]> lines = new ArrayList<>();

        // Read the file, replace commas with dots, and parse lines into arrays of strings
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace(',', '.'); // Replace commas with dots
                String[] columns = line.split("\\s+"); // Split by whitespace
                // Check if the last column can be parsed as a float
                try {
                    Float.parseFloat(columns[columns.length - 1]);
                    lines.add(columns);
                } catch (NumberFormatException e) {
                    // Skip the line if the last column is not a float
                    System.err.println("Skipping line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Sort the list by the last column in descending order
        lines.sort((a, b) -> {
            Float lastColumnA = Float.parseFloat(a[a.length - 1]);
            Float lastColumnB = Float.parseFloat(b[b.length - 1]);
            return lastColumnB.compareTo(lastColumnA);
        });

        // Print the sorted lines and add a mark after every 30 lines
        int count = 0;
        for (String[] columns : lines) {
            System.out.println(String.join(" ", columns));
            count++;
            if (count % 30 == 0) {
                System.out.println("----------");
            }
        }
    }
}
