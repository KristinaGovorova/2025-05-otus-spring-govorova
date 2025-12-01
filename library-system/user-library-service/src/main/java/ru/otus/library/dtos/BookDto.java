package ru.otus.library.dtos;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private int availableCopies;
}
