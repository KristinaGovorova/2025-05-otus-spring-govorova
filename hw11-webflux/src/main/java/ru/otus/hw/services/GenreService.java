package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.GenreDto;

public interface GenreService {

    Flux<GenreDto> findAll();

    Mono<GenreDto> findById(String id);

    Mono<GenreDto> create(GenreDto genreDto);

    Mono<GenreDto> update(String id, GenreDto genreDto);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsById(String id);
}