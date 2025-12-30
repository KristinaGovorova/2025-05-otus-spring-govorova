package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.BookDto;

public interface BookService {

    Flux<BookDto> findAll();

    Mono<BookDto> findById(String id);

    Flux<BookDto> findByAuthorId(String authorId);

    Flux<BookDto> findByGenreId(String genreId);

    Mono<BookDto> create(BookDto bookDto);

    Mono<BookDto> update(String id, BookDto bookDto);

    Mono<Void> deleteById(String id);
}