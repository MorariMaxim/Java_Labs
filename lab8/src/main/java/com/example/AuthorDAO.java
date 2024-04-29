package com.example;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class AuthorDAO {
    private Connection connection;

    static int counter = 0;

    public AuthorDAO() {
        try {
            connection = DatabaseConnectionManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAuthor(String name) {

        String insertAuthor = "INSERT INTO authors (id, name) VALUES (?, ?)";

        if (getAuthorId(name) != -1)
            return;

        try (PreparedStatement insertAuthorStatement = connection.prepareStatement(insertAuthor);) {

            insertAuthorStatement.setInt(1, counter++);
            insertAuthorStatement.setString(2, name);

            try {
                insertAuthorStatement.executeUpdate();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public int getAuthorId(String name) {
        int authorId = -1; 

        String selectAuthorId = "SELECT id FROM authors WHERE name like ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAuthorId)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    authorId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving author ID: " + e.getMessage());
        }

        return authorId;
    }

    public Author getAuthorById(int id) {
        Author author = null;

        String selectAuthor = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAuthor)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    author = new Author(
                            resultSet.getInt("id"),
                            resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving author: " + e.getMessage());
        }

        return author;
    }

}