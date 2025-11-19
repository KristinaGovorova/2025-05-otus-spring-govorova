package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    @Transactional
    Genre save(Genre genre);

    @Transactional
    void deleteById(long id);
}
