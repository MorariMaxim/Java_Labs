package com.example.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NamedQuery(name = "Book.findByName", query = "SELECT b FROM Book b WHERE b.title LIKE :namePattern")
public class Book {
    @Id
    @GeneratedValue(generator = "book_id_seq")
    @GenericGenerator(name = "book_id_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "book_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String title;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
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

    @Override
    public String toString() {
        return "Book [title=" + title + ", publication=" + publication + ", authors=" + authors + ", publishingHouse="
                + publishingHouse + "]";
    }

    public Book(String title, Date publication, PublishingHouse publishingHouse) {
        this.title = title;
        this.publication = publication;
        this.publishingHouse = publishingHouse;
    }

}