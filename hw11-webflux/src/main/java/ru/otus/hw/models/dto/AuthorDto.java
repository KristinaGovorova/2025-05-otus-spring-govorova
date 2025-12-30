package ru.otus.hw.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorDto {
    private String id;

    @NotBlank(message = "Имя автора обязательно")
    private String fullName;
}