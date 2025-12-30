package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findByAuthorId(long authorId);

    List<BookDto> findByGenreId(long genreId);

    List<BookDto> findAll();

    BookDto insert(String title, long authorId, long genreId);

    BookDto update(long id, String title, long authorId, long genreId);

    BookDto insert(BookDto bookDto);

    BookDto update(BookDto bookDto);

    BookDto save(Book book);

    BookDto save(BookDto bookDto);

    void deleteById(long id);
}