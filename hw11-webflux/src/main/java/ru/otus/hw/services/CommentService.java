package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.CommentDto;

public interface CommentService {

    Flux<CommentDto> findAllByBookId(String bookId);

    Mono<CommentDto> findById(String id);

    Mono<CommentDto> create(CommentDto commentDto);

    Mono<CommentDto> update(String id, CommentDto commentDto);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteAllByBookId(String bookId);
}