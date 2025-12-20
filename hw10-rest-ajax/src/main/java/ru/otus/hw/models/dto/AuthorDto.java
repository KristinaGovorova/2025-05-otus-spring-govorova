package ru.otus.hw.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorDto {
    private Long id;

    @NotBlank(message = "Имя автора обязательно")
    private String fullName;
}