package com.server;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.DataBase.example.model.Author;
import com.DataBase.example.model.Book;
import com.DataBase.example.model.ReadingList;
import com.DataBase.example.persistence.EntityManagerFactorySingleton;
import com.DataBase.example.repository.Repository;
import com.server.Controllers.requests.BookRequest;

public class DataBaseServices {

    static Repository<Author> authorRepository = new Repository<>(
            EntityManagerFactorySingleton.getEntityManagerFactory(),
            Author.class);

    static Repository<Book> bookRepository = new Repository<>(
            EntityManagerFactorySingleton.getEntityManagerFactory(),
            Book.class);

    static Repository<ReadingList> ReadingListRepo = new Repository<>(
            EntityManagerFactorySingleton.getEntityManagerFactory(),
            ReadingList.class);

    static public List<Author> getAuthors(String namePattern) {

        return authorRepository.findByName(namePattern);
    }

    static public List<Book> getBooks(String namePattern) {

        return bookRepository.findByName(namePattern);
    }

    static public ReadingList getReadingList(Long id) {

        return ReadingListRepo.findById(id);
    }

    static public Book createBook(String title, String authorName) {
        try {
            Repository<Book> bookRepository = new Repository<>(
                    EntityManagerFactorySingleton.getEntityManagerFactory(),
                    Book.class);

            Book newBook = new Book(title);

            List<Author> foundAuthors = authorRepository.findByName("%" + authorName + "%");

            Author author;

            if (!foundAuthors.isEmpty()) {
                author = foundAuthors.get(0);
            } else
                author = new Author(authorName);

            newBook.addAuthor(author);

            return bookRepository.create(newBook);

        }

        catch (Exception e) {
            System.err.println("Error creating book: " + e.getMessage());
        }

        return null;
    }
    

    public static String changeBook(Long id, String newTitle) {

        Book foundBook = bookRepository.findById(id);

        if (foundBook != null) {

            foundBook.setTitle(newTitle);

            if (bookRepository.create(foundBook) == null)
                return "Server error";
            return "Book successfully updated";
        }

        else
            return "No such book id";

    }

    public static String deleteBook(Long id) {
        Book book = bookRepository.findById(id);

        if (book != null) {
            for (var author : book.getAuthors()) {
                authorRepository.delete(author);
            }

            bookRepository.delete(book);
            return "Successfully deleted book";
        }
        return "No such book id";
    }

    public static Book getBook(Long id) {

        return bookRepository.findById(id);

    }
}
