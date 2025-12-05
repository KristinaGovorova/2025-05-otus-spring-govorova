package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    @Transactional(readOnly = true)
    Optional<Book> findById(String id);

    List<Book> findAll();

    @Transactional
    Book insert(String title, String authorId, String genreId);

    @Transactional
    Book update(String id, String title, String authorId, String genreId);

    @Transactional
    void deleteById(String id);
}