package com.DataBase.example.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQuery(name = "Author.findByName", query = "SELECT a FROM Author a WHERE a.name LIKE :namePattern")

public class Author {
    @Id
    @GeneratedValue(generator = "author_id_seq")
    @GenericGenerator(name = "author_id_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "author_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String name;

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

    public Author(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "authors", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Book> books = new HashSet<>();

    public Author() {

    }

    @Override
    public String toString() {
        return "Author [name=" + name + "]";
    }

}
