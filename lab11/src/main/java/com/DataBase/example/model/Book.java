package com.DataBase.example.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.Serializers.BookSerializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NamedQuery(name = "Book.findByName", query = "SELECT b FROM Book b WHERE b.title LIKE :namePattern")
@JsonSerialize(using = BookSerializer.class)
public class Book {
    @Id
    @GeneratedValue(generator = "book_id_seq")
    @GenericGenerator(name = "book_id_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "book_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private Date publication;

    public Book(String title, Date publication) {
        this.title = title;
        this.publication = publication;
    }

    public Date getPublication() {
        return publication;
    }

    private String language;

    private int pages;

    public Book(String title) {
        this.title = title;
    }

    public Book() {

    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();

    public Set<Author> getAuthors() {
        return authors;
    }

    public void addAuthor(Author a) {

        authors.add(a);
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "pubhouse_id")
    private PublishingHouse publishingHouse;

    public PublishingHouse getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(PublishingHouse publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    @JoinTable(name = "reading_lists_books", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "readinglist_id"))
    private Set<ReadingList> readingsLists = new HashSet<>();

    public Set<ReadingList> getReadingsLists() {
        return readingsLists;
    }

    public void setReadingsLists(Set<ReadingList> readingsLists) {
        this.readingsLists = readingsLists;
    }

    public void addReadingList(ReadingList readingList) {
        readingsLists.add(readingList);
        readingList.getBooks().add(this);
    }

    public void removeReadingList(ReadingList readingList) {
        readingsLists.remove(readingList);
        readingList.getBooks().remove(this);
    }

    @Override
    public String toString() {
        return "Book [title=" + title + ", id = " + id + ", publication=" + publication + ", authors=" + authors
                + ", publishingHouse="
                + publishingHouse + "]";
    }

    public Book(String title, Date publication, PublishingHouse publishingHouse) {
        this.title = title;
        this.publication = publication;
        this.publishingHouse = publishingHouse;
    }

}