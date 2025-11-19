package ru.otus.hw.services;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findByAuthorId(long authorId);

    List<Book> findByGenreId(long genreId);

    List<Book> findAll();

    Book insert(String title, long authorId, long genreId);

    Book update(long id, String title, long authorId, long genreId);

    public Book save(Book book);

    void deleteById(long id);
}