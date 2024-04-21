package com.example;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnectionManager {
    private static final HikariDataSource dataSource;

    private DatabaseConnectionManager() {
    }

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe"); // JDBC URL for Oracle
        config.setUsername("STUDENT"); // Your Oracle username
        config.setPassword("STUDENT"); // Your Oracle password
        config.setMaximumPoolSize(10); // Maximum number of connections in the pool
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            System.out.println("Connected to Oracle Database.");

            // Execute a sample SQL statement
            String sqlStatement = "SELECT * FROM studenti";
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sqlStatement)) {
                // Process the result set
                while (resultSet.next()) {
                    // Retrieve data from the result set
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("nume");
                    // Process data as needed
                    System.out.println("ID: " + id + ", Name: " + name);
                }
            } catch (SQLException e) {
                System.err.println("Failed to execute SQL statement.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to Oracle Database.");
            e.printStackTrace();
        }
    }

}
