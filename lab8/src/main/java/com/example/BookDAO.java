package com.example;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO() {
        try {
            connection = DatabaseConnectionManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        String insertBook = "INSERT INTO books (id, title, language, publication, pages) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement insertBookStmt = connection.prepareStatement(insertBook);) {

            insertBookStmt.setInt(1, book.getId());
            insertBookStmt.setString(2, book.getTitle());
            insertBookStmt.setString(3, book.getLanguage());
            insertBookStmt.setDate(4, new java.sql.Date(book.getPublicationDate().getTime()));
            insertBookStmt.setInt(5, book.getNumberOfPages());

            try {
                insertBookStmt.executeUpdate();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Book getBookById(int id) {
        Book book = null; // Initialize book to null

        String selectBook = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectBook)) {
            // Set the id parameter in the prepared statement
            statement.setInt(1, id);

            // Execute the query to get the book details
            try (ResultSet resultSet = statement.executeQuery()) {
                // Check if a book with the given id exists
                if (resultSet.next()) {
                    // If a book is found, create a Book object
                    book = new Book(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("language"),
                            resultSet.getDate("publication"),
                            resultSet.getInt("pages"));

                    // Fetch authors for the book
                    List<Author> authors = getAuthorsForBook(id);
                    book.setAuthors(authors);

                    // Fetch genres for the book
                    List<String> genres = getGenresForBook(id);
                    book.setGenres(genres);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book: " + e.getMessage());
        }

        return book; // Return the book (or null if not found)
    }

    public List<Book> getBooks(int limit) {
        List<Book> allBooks = new ArrayList<>();

        int added = 0;

        String query = "SELECT id FROM books";

        try (PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next() && added < limit) {
                int bookId = resultSet.getInt("id");
                Book book = getBookById(bookId);
                if (book != null) {
                    allBooks.add(book);
                    added += 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all books: " + e.getMessage());
        }

        return allBooks;
    }

    private List<Author> getAuthorsForBook(int bookId) throws SQLException {
        List<Author> authors = new ArrayList<>();

        String selectAuthors = "SELECT a.id, a.name FROM authors a JOIN authorship au ON a.id = au.author_id WHERE au.book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAuthors)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Author author = new Author(resultSet.getInt("id"), resultSet.getString("name"));
                    authors.add(author);
                }
            }
        }

        return authors;
    }

    private List<String> getGenresForBook(int bookId) throws SQLException {
        List<String> genres = new ArrayList<>();

        String selectGenres = "SELECT genre_name FROM genres WHERE book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectGenres)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    genres.add(resultSet.getString("genre_name"));
                }
            }
        }

        return genres;
    }

    static String toSqlDateFormat(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MMMM/yyyy");
        return outputFormat.format(date);

    }
}