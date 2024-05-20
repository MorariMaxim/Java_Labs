package com.server.Controllers;

import java.util.List;

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

import com.Bonus.LongestRecommendedList;
import com.DataBase.example.model.Book;
import com.server.DataBaseServices;
import com.server.Controllers.requests.BookRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/books")
@Tag(name = "Books Management", description = "Operations related to books management")
public class BooksController {

    @GetMapping("")
    @Operation(summary = "Get all books", description = "Get a list of all books.")
    public List<Book> getBooks() {
        return DataBaseServices.getBooks("%");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Get detailed information about a book by its ID.")
    public ResponseEntity<Object> getBook(@PathVariable("id") Long id) {
        Book book = DataBaseServices.getBook(id);
        if (book == null) {
            return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    /*
     * @PostMapping("")
     * 
     * @Operation(summary = "Create a new book", description =
     * "Create a new book with the provided title and author name.")
     * public ResponseEntity<Book> createBook(
     * 
     * @RequestParam("title") String title,
     * 
     * @RequestParam("authorName") String authorName) {
     * 
     * Book created = DataBaseServices.createBook(title, authorName);
     * if (created != null) {
     * return ResponseEntity.status(HttpStatus.CREATED).body(created);
     * } else {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     * }
     * }
     */

    /*
     * @PutMapping("/{id}")
     * 
     * @Operation(summary = "Update a book's title", description =
     * "Update the title of an existing book by its ID.")
     * public String updateBook(
     * 
     * @PathVariable("id") Long id,
     * 
     * @RequestParam("newTitle") String newTitle) {
     * 
     * return DataBaseServices.changeBook(id, newTitle);
     * }
     */

    @PostMapping("")
    @Operation(summary = "Create a new book", description = "Create a new book with the provided title and author name.")
    public ResponseEntity<Book> createBook(@RequestBody BookRequest bookRequest) {
        String title = bookRequest.getTitle();
        String authorName = bookRequest.getAuthorName();

        Book created = DataBaseServices.createBook(title, authorName);
        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book's title", description = "Update the title of an existing book by its ID.")
    public ResponseEntity<String> updateBook(
            @PathVariable("id") Long id,
            @RequestBody BookRequest updatedBook) {

        // Assuming DataBaseServices.changeBookName is updated to accept a Book object
        String result = DataBaseServices.changeBook(id, updatedBook.getTitle());
        if (result.equals("Book updated successfully")) {
            return ResponseEntity.ok("Book updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update book");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Delete a book by its ID.")
    public String deleteBook(
            @PathVariable("id") Long id) {

        return DataBaseServices.deleteBook(id);
    }

    @GetMapping("longestRecommendedList")
    @Operation(summary = "Get the longest recommended list of books") 
    public List<Book> getLongestRecommendedList() {
        return LongestRecommendedList.solve();
    }
}
