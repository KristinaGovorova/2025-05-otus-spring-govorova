package ru.otus.book.services;

import ru.otus.book.models.Author;

import java.util.List;

public interface AuthorService {

    List<Author> findAll();

    Author save(Author author);

    void deleteById(Long id);
}
