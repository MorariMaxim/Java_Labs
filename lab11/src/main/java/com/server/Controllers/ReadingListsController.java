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
import com.DataBase.example.model.ReadingList;
import com.fromLabSlides.Product;
import com.server.DataBaseServices;
@RestController
@RequestMapping("/readingLists")
public class ReadingListsController {

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducts(@PathVariable("id") Long id) {
        ReadingList readingList = DataBaseServices.getReadingList(id);
        if (readingList != null) {
            return ResponseEntity.ok(readingList);  
        } else {
            String errorMessage = "Reading list with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    
}
