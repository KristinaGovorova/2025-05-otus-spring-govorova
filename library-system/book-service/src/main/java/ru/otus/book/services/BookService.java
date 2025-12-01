package ru.otus.book.services;

import ru.otus.book.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findByAuthorId(long authorId);

    List<Book> findByGenreId(long genreId);

    List<Book> findAll();

    public Book save(Book book);

    void deleteById(long id);
}