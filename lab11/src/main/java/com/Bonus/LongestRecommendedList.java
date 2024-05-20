package com.Bonus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DataBase.example.model.Book;
import com.DataBase.example.persistence.EntityManagerFactorySingleton;
import com.DataBase.example.repository.Repository;
import com.server.DataBaseServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/longestRecommendedList")
@Tag(name = "Bonus Service", description = "Returns the longest recommended list of books")
public class LongestRecommendedList {

    @GetMapping("")
    @Operation(summary = "Get the longest recommended list of books") 
    public List<Book> getLongestRecommendedList() {
        return solve();
    }

    public static List<Book> solve() {
        List<Book> books = DataBaseServices.getBooks("%");

        int booksSize = books.size();

        final double chance = 0.8;

        Boolean[][] precedence = new Boolean[booksSize][booksSize];

        Random rand = new Random();

        for (int firstBookIndex = 0; firstBookIndex < booksSize - 1; firstBookIndex++) {

            for (int secondBookIndex = firstBookIndex + 1; secondBookIndex < booksSize; secondBookIndex++) {

                Book firstBook = books.get(firstBookIndex);
                Book secondBook = books.get(secondBookIndex);

                if (firstBook.getPublication() != null && secondBook.getPublication() != null
                        && firstBook.getPublication().compareTo(secondBook.getPublication()) < 0) {

                    if (rand.nextDouble() < chance) {
                        precedence[firstBookIndex][secondBookIndex] = true;

                        System.out.println(books.get(firstBookIndex).getPublication() + " precedes"
                                + books.get(secondBookIndex).getPublication());
                    }
                }
            }
        }

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

        return path.stream().map(vertex -> books.get(vertex)).collect(Collectors.toList());

    }

    static String yearFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);

    }
}
