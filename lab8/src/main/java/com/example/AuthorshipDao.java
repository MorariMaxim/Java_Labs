package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorshipDao {
    private Connection connection;

    public AuthorshipDao() {
        try {
            connection = DatabaseConnectionManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAuthorship(int bookId, int authorId) {
        String insertAuthorship = "INSERT INTO authorship (book_id, author_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertAuthorship)) {
            statement.setInt(1, bookId);
            statement.setInt(2, authorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding authorship: " + e.getMessage());
        }
    }

    public List<Author> getAuthorsForBook(int bookId) {
        List<Author> authors = new ArrayList<>();

        String selectAuthors = "SELECT author_id FROM authorship WHERE book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAuthors)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");

                    AuthorDAO authorDAO = new AuthorDAO();
                    authors.add(authorDAO.getAuthorById(authorId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving authors for book: " + e.getMessage());
        }

        return authors;
    }
}
