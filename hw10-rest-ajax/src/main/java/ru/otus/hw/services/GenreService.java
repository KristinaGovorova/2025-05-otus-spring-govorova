package ru.otus.hw.services;

import ru.otus.hw.models.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(long id);

    GenreDto insert(GenreDto genreDto);

    GenreDto update(GenreDto genreDto);

    void deleteById(long id);
}