package ru.otus.hw.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Comment {
    private String id = new ObjectId().toString();

    private String text;

    public Comment(String text) {
        this.text = text;
    }
}