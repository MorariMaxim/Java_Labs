package com.example;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import com.example.bonus.*;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.PublishingHouse;
import com.example.persistence.EntityManagerFactorySingleton;
import com.example.repository.Repository;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";

    // System.out.println(RED + "This is red text" + RESET);
    public static void main(String[] args) {

        Repository<Book> bookRepository = new Repository<>(EntityManagerFactorySingleton.getEntityManagerFactory(),
                Book.class);

        String[] titles = {
                "Alice in Wonderland",
                "A Tale of Two Cities",
                "The Great Gatsby",
                "To Kill a Mockingbird",
                "Pride and Prejudice"
        };

        int[] years = { 1865, 1932, 1945, 1949, 1925 };

        String[] authors = {
                "Lewis Carroll",
                "Charles Dickens",
                "F. Scott Fitzgerald",
                "Harper Lee",
                "Jane Austen"
        };

        String[] publicationHouses = {
                "Macmillan Publishers",
                "Chapman & Hall",
                "Charles Scribner's Sons",
                "J. B. Lippincott & Co.",
                "T. Egerton, Whitehall"
        };

        for (int i = 0; i < titles.length; i++) {

            Author author = new Author(authors[i]);
            Date year = ChocoSolverDemo.getDateFromYear(years[i]);
            Book book = new Book(titles[i], year, new PublishingHouse(publicationHouses[i]));
            book.addAuthor(author);

            bookRepository.create(book);
        }
        
        List<Book> foundBook = bookRepository.findByName("A%");

        System.out.println("found Books:");

        System.out.println(foundBook);

        EntityManagerFactorySingleton.closeEntityManagerFactory();
    }
}
