package ru.otus.hw.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

import java.time.LocalDateTime;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    Flux<Comment> findByBookId(String bookId);

    Flux<Comment> findByBookId(String bookId, Pageable pageable);

    Flux<Comment> findByBookIdOrderByCreatedAtDesc(String bookId);

    Flux<Comment> findByBookIdOrderByCreatedAtAsc(String bookId);

    @Query("{ 'text': { $regex: ?0, $options: 'i' } }")
    Flux<Comment> findByTextContainingIgnoreCase(String text);

    Flux<Comment> findByCreatedAtAfter(LocalDateTime date);

    Flux<Comment> findByCreatedAtBefore(LocalDateTime date);

    Flux<Comment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Flux<Comment> findByBookIdAndCreatedAtBetween(String bookId,
                                                  LocalDateTime start,
                                                  LocalDateTime end);

    @Query(value = "{ 'book.$id': ?0 }", sort = "{ 'createdAt': -1 }")
    Flux<Comment> findTopNByBookIdOrderByCreatedAtDesc(String bookId, Pageable pageable);

    Mono<Long> countByBookId(String bookId);

    Mono<Void> deleteByBookId(String bookId);

    Mono<Void> deleteByCreatedAtBefore(LocalDateTime date);

    Mono<Boolean> existsByBookId(String bookId);

    @Query(value = "{ 'book.$id': ?0 }",
            fields = "{ 'text': 1, 'createdAt': 1, 'book.$id': 1 }")
    Flux<Comment> findCommentsWithBookInfo(String bookId);
}