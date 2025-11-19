package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> findAll();

    Optional<Author> findById(long id);

    @Transactional
    Author save(Author author);

    @Transactional
    void deleteById(long id);
}
