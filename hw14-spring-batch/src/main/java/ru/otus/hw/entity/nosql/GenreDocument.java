package ru.otus.hw.entity.nosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "genres")
@Data
@NoArgsConstructor
public class GenreDocument {

    @Id
    private String id;

    private String name;

    @Field("original_id")
    private Long originalId;
}