package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) {

        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "STUDENT";
        String password = "STUDENT";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to Oracle Database.");

            String scriptFilePath = "create_tables.sql";
            String scriptContent = new String(Files.readAllBytes(Paths.get(scriptFilePath)));

            String[] sqlStatements = scriptContent.split(";");            

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

        } catch (SQLException | IOException e) {
            System.err.println("Failed to connect to Oracle Database or read SQL script file.");
            e.printStackTrace();
        }

    }
}
