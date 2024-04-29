package com.example;

import java.time.LocalDate; 
import java.util.List; 

public class ReadingList {

    private LocalDate creationTimeStam;
    private String name;
    private List<Book> books;
    public void setCreationTimeStam(LocalDate creationTimeStam) {
        this.creationTimeStam = creationTimeStam;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBooks(List<Book> books) {
        this.books = books;
    }
    @Override
    public String toString() {
        return "ReadingList [creationTimeStam=" + creationTimeStam + ", name=" + name + ", books=" + books + "]";
    }

    
}
