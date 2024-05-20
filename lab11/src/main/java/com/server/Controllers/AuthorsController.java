package com.server.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DataBase.example.model.Author;
import com.server.DataBaseServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/authors")
@Tag(name = "Books Management", description = "Operations related to books management")
public class AuthorsController {

    @GetMapping
    @Operation(summary = "Get all authors", description = "Get a list of all authors.")
    public List<Author> getProducts() {
        return DataBaseServices.getAuthors("%");
    }

}
