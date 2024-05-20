package com.server.Serializers;

import com.DataBase.example.model.Book;
import com.DataBase.example.model.ReadingList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ReadingListSerializer extends JsonSerializer<ReadingList> {

    @Override
    public void serialize(ReadingList readingList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", readingList.getId());
        jsonGenerator.writeStringField("name", readingList.getName());
        jsonGenerator.writeFieldName("books");
        jsonGenerator.writeStartArray();
        for (Book book : readingList.getBooks()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", book.getId());
            jsonGenerator.writeStringField("title", book.getTitle());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
