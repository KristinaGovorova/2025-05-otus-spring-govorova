package ru.otus.hw.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDto {
    private String id;

    @NotBlank(message = "Название книги обязательно")
    private String title;

    @NotNull(message = "Автор обязателен")
    private String authorId;

    private String authorName;

    @NotNull(message = "Жанр обязателен")
    private String genreId;

    private String genreName;
}