package com.Bonus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DataBase.example.model.Book;
import com.DataBase.example.persistence.EntityManagerFactorySingleton;
import com.DataBase.example.repository.Repository;
import com.server.DataBaseServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/longestRecommendedList")
@Tag(name = "Bonus Service", description = "Returns the longest recommended list of books")
public class LongestRecommendedList {
    /*
     * @GetMapping("")
     * 
     * @Operation(summary = "Get the list")
     * public List<Book> getList() {
     * 
     * List<Book> books = DataBaseServices.getBooks("%");
     * 
     * return solve(books);
     * }
     */

    public static void main(String[] args) {
        List<Book> books = DataBaseServices.getBooks("%");

        int booksSize = books.size();

        final double chance = 0.8;

        Boolean[][] precedence = new Boolean[booksSize][booksSize];

        Random rand = new Random();

        for (int firstBook = 0; firstBook < booksSize - 1; firstBook++) {

            for (int secondBook = firstBook + 1; secondBook < booksSize; secondBook++) {

                if (books.get(firstBook).getPublication().compareTo(books.get(secondBook).getPublication()) < 0) {

                    if (rand.nextDouble() < chance) {
                        precedence[firstBook][secondBook] = true;

                        System.out.println(books.get(firstBook).getPublication() + " precedes"
                                + books.get(secondBook).getPublication());
                    }
                }
            }
        }
        /*
         * Map<Long, Integer> idToVertex = new HashMap<>();
         * Map<Integer, Long> vertexToId = new HashMap<>();
         * 
         * for (int i = 0; i < booksSize; i++) {
         * vertexToId.put(i, books.get(i).getId());
         * idToVertex.put(books.get(i).getId(), i);
         * }
         */

        Graph graph = new Graph(booksSize);

        for (int i = 0; i < precedence.length; i++) {

            for (int j = 0; j < precedence[i].length; j++) {

                if (Boolean.TRUE.equals(precedence[i][j]))
                    graph.addEdge(i, j);
            }
        }

        var path = graph.findLongestPath(booksSize);
        System.out.println("Path by vertices:");
        System.out.println(path);

        for (Integer integer : path) {
            var book = books.get(integer);
            System.out.print(book.getTitle() + ", " + yearFromDate(book.getPublication()) + " -> ");
        }
    }

    static String yearFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);

    }
}
