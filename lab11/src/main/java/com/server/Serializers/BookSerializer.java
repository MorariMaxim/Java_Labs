package com.server.Serializers;

import com.DataBase.example.model.Author;
import com.DataBase.example.model.Book;
import com.DataBase.example.model.ReadingList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class BookSerializer extends JsonSerializer<Book> {

    @Override
    public void serialize(Book book, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", book.getId());
        jsonGenerator.writeStringField("title", book.getTitle());
        Date publicationDate = book.getPublication();
        if (publicationDate != null) {
            jsonGenerator.writeStringField("publicationDate", getFormatedDate(publicationDate));
        } else {
            jsonGenerator.writeNullField("publicationDate");
        }

        jsonGenerator.writeArrayFieldStart("readingLists");
        for (ReadingList readingList : book.getReadingsLists()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", readingList.getId());
            jsonGenerator.writeStringField("name", readingList.getName());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("authors");
        Set<Author> authors = book.getAuthors();
        for (Author author : authors) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", author.getId());
            jsonGenerator.writeStringField("name", author.getName());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    String getFormatedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Choose your desired date format
        return dateFormat.format(date);
    }
}
