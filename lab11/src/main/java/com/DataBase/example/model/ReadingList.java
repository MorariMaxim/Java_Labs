package com.DataBase.example.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.Serializers.ReadingListSerializer;

import java.util.HashSet;
import java.util.Set;
 

@Entity
@NamedQuery(name = "ReadingList.findByName", query = "SELECT rl FROM ReadingList rl WHERE rl.name LIKE :namePattern")
@JsonSerialize(using = ReadingListSerializer.class)
public class ReadingList {
    @Id
    @GeneratedValue(generator = "readinglist_id_seq")
    @GenericGenerator(name = "readinglist_id_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "readinglist_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String name;

    public ReadingList(String name) {
        this.name = name;
    }

    public ReadingList() {
    }

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "readingsLists", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Book> books = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        books.add(book);
        book.getReadingsLists().add(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.getReadingsLists().remove(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReadingList [id=").append(id).append(", name=").append(name).append(", books=[");

        // Iterate over the books collection and append their names
        for (Book book : books) {
            sb.append(book.getTitle()).append(", "); // Assuming getTitle() returns the name of the book
        }

        // Remove the trailing comma and space if there are books
        if (!books.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("]]");
        return sb.toString();
    }

}
