package com.DataBase.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityTransaction;

import com.DataBase.example.model.Author;
import com.DataBase.example.model.Book;
import com.DataBase.example.model.PublishingHouse;
import com.DataBase.example.model.ReadingList;
import com.DataBase.example.persistence.EntityManagerFactorySingleton;
import com.DataBase.example.repository.Repository;

public class Main {
        public static final String RESET = "\u001B[0m";
        public static final String BLUE = "\u001B[34m";

        // System.out.println(RED + "This is red text" + RESET);
        public static void main(String[] args) {

                Repository<Book> bookRepository = new Repository<>(
                                EntityManagerFactorySingleton.getEntityManagerFactory(),
                                Book.class);

                Repository<Author> authorRepository = new Repository<>(
                                EntityManagerFactorySingleton.getEntityManagerFactory(),
                                Author.class);

                Repository<ReadingList> ReadingListRepo = new Repository<>(
                                EntityManagerFactorySingleton.getEntityManagerFactory(),
                                ReadingList.class);

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
                        Date year = getDateFromYear(years[i]);
                        Book book = new Book(titles[i], year, new PublishingHouse(publicationHouses[i]));
                        book.addAuthor(author);

                        // bookRepository.create(book);
                }

                List<Book> foundBooks = bookRepository.findByName("%");
                System.out.println("books: ");

                for (Book book : foundBooks) {

                        System.out.println(book.getId() + ", " + book.getTitle() + ", " + book.getPublication());
                }

                

                if (false)
                        for (var entry : books) {

                                List<Book> existingBooks = bookRepository.findByName(entry[0]);

                                if (existingBooks.isEmpty()) {
                                        Book newBook = new Book(entry[0], getDateFromYear(entry[2]));

                                        List<Author> existingAuthors = authorRepository.findByName(entry[1]);

                                        if (existingAuthors.isEmpty()) {

                                                newBook.addAuthor(new Author(entry[1]));
                                        }

                                        bookRepository.create(newBook);
                                }
                        }
                EntityManagerFactorySingleton.closeEntityManagerFactory();

        }

        public static Date getDateFromYear(int year) {

                return Date.from(LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        }

        static String[][] books = {
                        { "The Iliad", "Homer", "800" },
                        { "The Art of War", "Sun Tzu", "500" },
                        { "The Republic", "Plato", "380" },
                        { "The Divine Comedy", "Dante Alighieri", "1320" },
                        { "Hamlet", "William Shakespeare", "1603" },
                        { "Pride and Prejudice", "Jane Austen", "1813" },
                        { "Moby-Dick", "Herman Melville", "1851" },
                        { "War and Peace", "Leo Tolstoy", "1869" },
                        { "1984", "George Orwell", "1949" },
                        { "To Kill a Mockingbird", "Harper Lee", "1960" },
                        { "One Hundred Years of Solitude", "Gabriel García Márquez", "1967" },
                        { "The Catcher in the Rye", "J.D. Salinger", "1951" },
                        { "Harry Potter and the Philosopher's Stone", "J.K. Rowling", "1997" },
                        { "The Da Vinci Code", "Dan Brown", "2003" },
                        { "The Hunger Games", "Suzanne Collins", "2008" },
                        { "Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "2011" },
                        { "The Testaments", "Margaret Atwood", "2019" },
                        { "Frankenstein", "Mary Shelley", "1818" },
                        { "Alice's Adventures in Wonderland", "Lewis Carroll", "1865" },
                        { "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", "1892" },
                        { "Dracula", "Bram Stoker", "1897" },
                        { "The Great Gatsby", "F. Scott Fitzgerald", "1925" },
                        { "Brave New World", "Aldous Huxley", "1932" },
                        { "Animal Farm", "George Orwell", "1945" },
                        { "Lord of the Flies", "William Golding", "1954" },
                        { "Catch-22", "Joseph Heller", "1961" },
                        { "Dune", "Frank Herbert", "1965" },
                        { "The Godfather", "Mario Puzo", "1969" },
                        { "Jurassic Park", "Michael Crichton", "1990" },
                        { "The Road", "Cormac McCarthy", "2006" },
                        { "Gone Girl", "Gillian Flynn", "2012" },
                        { "The Silent Patient", "Alex Michaelides", "2019" },
                        { "The Alchemist", "Paulo Coelho", "1988" },
                        { "The Hobbit", "J.R.R. Tolkien", "1937" },
                        { "The Lord of the Rings", "J.R.R. Tolkien", "1954-1955" },
                        { "A Game of Thrones", "George R.R. Martin", "1996" },
                        { "The Girl with the Dragon Tattoo", "Stieg Larsson", "2005" },
                        { "The Martian", "Andy Weir", "2011" },
                        { "The Help", "Kathryn Stockett", "2009" },
                        { "The Fault in Our Stars", "John Green", "2012" },
                        { "The Picture of Dorian Gray", "Oscar Wilde", "1890" },
                        { "Treasure Island", "Robert Louis Stevenson", "1883" },
                        { "Wuthering Heights", "Emily Brontë", "1847" },
                        { "Crime and Punishment", "Fyodor Dostoevsky", "1866" },
                        { "Les Misérables", "Victor Hugo", "1862" },
                        { "The Count of Monte Cristo", "Alexandre Dumas", "1844" },
                        { "The Adventures of Tom Sawyer", "Mark Twain", "1876" },
                        { "Around the World in Eighty Days", "Jules Verne", "1873" },
                        { "A Tale of Two Cities", "Charles Dickens", "1859" },
                        { "Oliver Twist", "Charles Dickens", "1838" },
                        { "Great Expectations", "Charles Dickens", "1861" },
                        { "Anna Karenina", "Leo Tolstoy", "1877" },
                        { "The Brothers Karamazov", "Fyodor Dostoevsky", "1880" },
                        { "Don Quixote", "Miguel de Cervantes", "1605" },
                        { "Dr. Jekyll and Mr. Hyde", "Robert Louis Stevenson", "1886" },
                        { "The Call of the Wild", "Jack London", "1903" },
                        { "The Adventures of Huckleberry Finn", "Mark Twain", "1884" },
                        { "Heart of Darkness", "Joseph Conrad", "1899" },
                        { "The Scarlet Letter", "Nathaniel Hawthorne", "1850" },
                        { "The Jungle Book", "Rudyard Kipling", "1894" },
                        { "The Three Musketeers", "Alexandre Dumas", "1844" },
                        { "Robinson Crusoe", "Daniel Defoe", "1719" },
                        { "Gulliver's Travels", "Jonathan Swift", "1726" },
                        { "Frankenstein", "Mary Shelley", "1818" },
                        { "Alice's Adventures in Wonderland", "Lewis Carroll", "1865" },
                        { "The Strange Case of Dr. Jekyll and Mr. Hyde", "Robert Louis Stevenson", "1886" },
                        { "The Time Machine", "H.G. Wells", "1895" },
                        { "The War of the Worlds", "H.G. Wells", "1898" },
                        { "Peter Pan", "J.M. Barrie", "1911" },
                        { "The Wind in the Willows", "Kenneth Grahame", "1908" },
                        { "Winnie-the-Pooh", "A.A. Milne", "1926" },
                        { "Mary Poppins", "P.L. Travers", "1934" },
                        { "Charlotte's Web", "E.B. White", "1952" },
                        { "Lord of the Flies", "William Golding", "1954" },
                        { "To Kill a Mockingbird", "Harper Lee", "1960" },
                        { "A Wrinkle in Time", "Madeleine L'Engle", "1962" },
                        { "Charlie and the Chocolate Factory", "Roald Dahl", "1964" },
                        { "Watership Down", "Richard Adams", "1972" },
                        { "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "1979" },
                        { "Matilda", "Roald Dahl", "1988" },
                        { "Harry Potter and the Philosopher's Stone", "J.K. Rowling", "1997" },
                        { "The Lightning Thief", "Rick Riordan", "2005" },
                        { "The Hunger Games", "Suzanne Collins", "2008" },
                        { "The Maze Runner", "James Dashner", "2009" },
                        { "Wonder", "R.J. Palacio", "2012" },
                        { "The Fault in Our Stars", "John Green", "2012" },
                        { "Harry Potter and the Cursed Child", "J.K. Rowling, Jack Thorne, John Tiffany", "2016" },
                        { "To All the Boys I've Loved Before", "Jenny Han", "2014" }

        };

        public static Date getDateFromYear(String year) {
                try {
                        // Parse the date string using a SimpleDateFormat with the desired format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                        Date date = dateFormat.parse(year);

                        // Set the date in a Calendar object with Julian calendar
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        // Get the Date object from the Calendar
                        return calendar.getTime();
                } catch (ParseException e) {
                        // Handle parsing errors
                        e.printStackTrace();
                        return null;
                }
        }
}
