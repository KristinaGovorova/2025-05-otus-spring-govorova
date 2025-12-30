package ru.otus.hw.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDto {
    private Long id;

    @NotBlank(message = "Название книги обязательно")
    private String title;

    @NotNull(message = "Автор обязателен")
    private Long authorId;

    private String authorName;

    @NotNull(message = "Жанр обязателен")
    private Long genreId;

    private String genreName;
}