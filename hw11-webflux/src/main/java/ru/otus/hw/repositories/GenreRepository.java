package ru.otus.hw.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Genre;

public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {

    Mono<Genre> findByName(String name);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Flux<Genre> findByNameContainingIgnoreCase(String namePart);

    Flux<Genre> findAllByOrderByNameAsc();

    Flux<Genre> findAllBy(Pageable pageable);

    Flux<Genre> findAllByIdIn(Iterable<String> ids);

    Mono<Boolean> existsByName(String name);

    Mono<Void> deleteByName(String name);

    @Query(value = "{ 'books.genre._id': ?0 }", count = true)
    Mono<Long> countBooksByGenreId(String genreId);
}