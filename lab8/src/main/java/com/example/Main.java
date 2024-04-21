package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // JDBC connection parameters
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; // JDBC connection URL
        String username = "STUDENT"; // Database username
        String password = "STUDENT"; // Database password

        // Establish a connection to the Oracle Database
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to Oracle Database.");

            // Read SQL script file
            String scriptFilePath = "create_tables.sql"; // Path to your SQL script file
            StringBuilder scriptContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Remove semicolon from each line
                    line = line.trim();
                    if (!line.isEmpty()) {
                        scriptContent.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read SQL script file.");
                e.printStackTrace();
                return;
            }

            // Split script content into individual SQL statements
            String[] sqlStatements = scriptContent.toString().split(";");

            // Execute each SQL statement individually
            try (Statement statement = connection.createStatement()) {
                for (String sqlStatement : sqlStatements) {
                    statement.executeUpdate(sqlStatement);
                    System.out.println("Executed SQL statement: " + sqlStatement);
                }
                System.out.println("All SQL statements executed successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to execute SQL statements.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Failed to connect to Oracle Database.");
            e.printStackTrace();
        }

    }

}