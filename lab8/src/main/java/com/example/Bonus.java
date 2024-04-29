package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.eq.*;
import org.graph4j.util.VertexSet;
 

public class Bonus {

    static Graph booksGraph;

    public static void main(String[] args) {

        BookDAO bookDAO = new BookDAO();

        List<Book> books = bookDAO.getBooks(200);

        /*
         * List<Book> books = new ArrayList<>();
         * Book book = new Book(0, null, null, null, 0);
         * 
         * book.setAuthors(Arrays.asList(new Author(0, "JKROWLING")));
         * book.setGenres(new ArrayList<>());
         * books.add(book);
         * books.add(book);
         * book = new Book(0, null, null, null, 0);
         * book.setAuthors(new ArrayList<>());
         * book.setGenres(new ArrayList<>());
         * books.add(book);
         * 
         * books.add(book);
         */

        

        booksGraph = constructGraph(books);

        createReadingLists(booksGraph, books);

    }

    static Graph constructGraph(List<Book> books) {

        GraphBuilder graphBuilder = GraphBuilder.numVertices(books.size());

        int book1Id = 0;
        int book2Id = 0;
        for (Book book1 : books) {
            book2Id = 0;
            for (Book book2 : books) {
                boolean addEdge = false;

                Set<Author> authors1 = new HashSet<>(book1.getAuthors());
                Set<Author> authors2 = new HashSet<>(book2.getAuthors());

                authors1.retainAll(authors2);

                if (!authors1.isEmpty())
                    addEdge = true;

                if (!addEdge) {

                    Set<String> genres1 = new HashSet<>(book1.getGenres());
                    Set<String> genres2 = new HashSet<>(book2.getGenres());

                    genres1.retainAll(genres2);
                    if (!genres1.isEmpty())
                        addEdge = true;

                }

                if (addEdge) {
                    graphBuilder.addEdge(book1Id, book2Id);
                }

                book2Id += 1;
            }
            book1Id += 1;
        }

        return graphBuilder.buildGraph();
    }

    static void createReadingLists(Graph graph, List<Book> books) {

        BacktrackEquitableColoring backtrackEquitableColoring = new BacktrackEquitableColoring(graph);

        Coloring coloring = backtrackEquitableColoring.findColoring();

        Map<Integer, VertexSet> classes = coloring.getColorClasses();

        List<ReadingList> readingLists = new ArrayList<>();

        int counter = 0;
        for (var entry : classes.entrySet()) {

            var set = entry.getValue();

            ReadingList readingList = new ReadingList();

            readingList.setName("readinglist#" + counter++);
            readingList.setCreationTimeStam(LocalDate.now());
            List<Book> booksList = new ArrayList<>();

            for (var index : set) {
                booksList.add(books.get(index));
            }

            readingList.setBooks(booksList);

            readingLists.add(readingList);
        }
        for (var list : readingLists) {

            System.out.println(list);

        }
    }
}
