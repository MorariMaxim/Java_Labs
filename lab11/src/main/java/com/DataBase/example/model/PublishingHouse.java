package com.DataBase.example.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQuery(name = "PublishingHouse.findByName", query = "SELECT ph FROM PublishingHouse ph WHERE ph.name LIKE :namePattern")
public class PublishingHouse {
    @Id
    @GeneratedValue(generator = "pubhouse_id_seq")
    @GenericGenerator(name = "pubhouse_id_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "pubhouse_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String name;

    public PublishingHouse(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "publishingHouse", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Book> books = new HashSet<>();

    public PublishingHouse() {

    }

    @Override
    public String toString() {
        return "PublishingHouse [name=" + name + "]";
    }
    
    
}
