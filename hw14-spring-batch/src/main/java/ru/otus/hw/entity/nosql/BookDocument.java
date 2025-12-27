package ru.otus.hw.entity.nosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "books")
@Data
@NoArgsConstructor
public class BookDocument {

    @Id
    private String id;

    private String title;

    @DBRef
    private AuthorDocument author;

    @DBRef
    private GenreDocument genre;

    @Field("publication_year")
    private Integer publicationYear;

    @Field("migrated_at")
    private LocalDateTime migratedAt;
}

