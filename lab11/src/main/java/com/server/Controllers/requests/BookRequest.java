package com.server.Controllers.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {
    @JsonProperty("title")
    private String title;

    @JsonProperty("authorName")
    private String authorName;

    @Override
    public String toString() {
        return "BookRequest [title=" + title + ", authorName=" + authorName + "]";
    }
}
