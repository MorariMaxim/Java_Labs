package com.example;

import java.util.List;

public class Book {
    private int id;
    private String title;
    private String language;
    private java.util.Date publicationDate;
    private int numberOfPages;

    private List<Author> authors;
    private List<String> genres;

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Book(int id, String title, String language, java.util.Date publicationDate, int numberOfPages) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.publicationDate = publicationDate;
        this.numberOfPages = numberOfPages;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getLanguage() {
        return language;
    }

    public java.util.Date getPublicationDate() {
        return publicationDate;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String toString() {
        return "Book [title=" + title + "]";
    }

    

    
}
