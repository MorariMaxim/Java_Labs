package com.server;

import java.util.List;

import com.DataBase.example.model.Author;
import com.DataBase.example.model.Book;
import com.DataBase.example.model.ReadingList;
import com.DataBase.example.persistence.EntityManagerFactorySingleton;
import com.DataBase.example.repository.Repository;

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

    static public Boolean createBook(String title, String authorName) {
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

            bookRepository.create(newBook);

            System.out.println("Created a new book: " + newBook);

            return true;
        }

        catch (Exception e) {
            System.err.println("Error creating book: " + e.getMessage());
        }

        return false;
    }

    public static String changeBookName(Long id, String newTItle) {
        Book book = bookRepository.findById(id);

        if (book != null) {
            book.setTitle(newTItle);

            bookRepository.create(book);

            return "Changed book title";
        }

        else {
            return "No such book id";
        }
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
