package ru.otus.hw.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

    Mono<Author> findByFullName(String fullName);

    @Query("{ 'fullName': { $regex: ?0, $options: 'i' } }")
    Flux<Author> findByFullNameContainingIgnoreCase(String namePart);

    Flux<Author> findAllByOrderByFullNameAsc();

    Flux<Author> findAllBy(Pageable pageable);

    Flux<Author> findAllByIdIn(Iterable<String> ids);

    Mono<Boolean> existsByFullName(String fullName);

    Mono<Void> deleteByFullName(String fullName);

    @Query(value = "{ 'books.author._id': ?0 }", count = true)
    Mono<Long> countBooksByAuthorId(String authorId);
}