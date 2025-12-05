package ru.otus.hw.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "authors")
public class Author {
    @Id
    private String id;

    @Field(name = "full_name")
    private String fullName;

    public Author(String fullName) {
        this.fullName = fullName;
    }
}