package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenresDao {
    private Connection connection;

    public GenresDao() {
        try {
            connection = DatabaseConnectionManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGenreToBook(int bookId, String genre) {
        try {
            if (exists(bookId, genre))
                return;

            String insertAuthorship = "INSERT INTO genres (book_id, genre_name) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(insertAuthorship)) {
                statement.setInt(1, bookId);
                statement.setString(2, genre);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error adding genre to book: " + e.getMessage());
            }
        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    private boolean exists(int bookId, String genre) throws SQLException {
        String query = "SELECT 1 FROM genres WHERE book_id = ? AND genre_name like ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);
            statement.setString(2, genre);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); 
            }
        }
    }

}
