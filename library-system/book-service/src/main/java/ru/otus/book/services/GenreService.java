package ru.otus.book.services;

import ru.otus.book.models.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findAll();

    Genre save(Genre genre);

    void deleteById(Long id);
}
