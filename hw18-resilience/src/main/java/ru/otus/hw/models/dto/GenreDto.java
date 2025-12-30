package ru.otus.hw.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreDto {
    private Long id;

    @NotBlank(message = "Название жанра обязательно")
    private String name;
}