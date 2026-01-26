package ru.otus.hw.entity.nosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "authors")
@Data
@NoArgsConstructor
public class AuthorDocument {

    @Id
    private String id;

    private String name;

    @Field("original_id")
    private Long originalId;
}
