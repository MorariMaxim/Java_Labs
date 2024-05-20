package com.server.Controllers;

import java.util.List;
import java.util.Map;

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
import com.server.DataBaseServices;
import com.server.Controllers.requests.BookRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @PostMapping("")
    @Operation(summary = "Create a new book", description = "Create a new book with the provided title and author name.")
    public ResponseEntity<Book> createBook(@RequestBody Book newBook) {
        Book createdBook = DataBaseServices.createBook(newBook.getTitle(), null);
        if (createdBook != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book's title", description = "Update the title of an existing book by its ID.")
    public ResponseEntity<String> updateBook(
            @PathVariable("id") Long id,
            @RequestBody BookRequest updatedBookRequest) {

        String result = DataBaseServices.changeBook(id, updatedBookRequest);
        if (result.equals("Book successfully updated")) {
            return ResponseEntity.ok("Book successfully updated");
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
}