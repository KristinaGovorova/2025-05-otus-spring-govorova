package ru.otus.hw.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithDetails;

import java.util.List;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Flux<Book> findByAuthorId(String authorId);

    Flux<Book> findByAuthorId(String authorId, Pageable pageable);

    Flux<Book> findByGenreId(String genreId);

    Flux<Book> findByGenreId(String genreId, Pageable pageable);

    @Query("{ 'author.fullName': ?0 }")
    Flux<Book> findByAuthorFullName(String authorFullName);

    @Query("{ 'genre.name': ?0 }")
    Flux<Book> findByGenreName(String genreName);

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Flux<Book> findByTitleContainingIgnoreCase(String titlePart);

    @Query("{ 'genre._id': { $in: ?0 } }")
    Flux<Book> findByGenreIds(List<String> genreIds);

    @Query("{ 'author._id': { $in: ?0 } }")
    Flux<Book> findByAuthorIds(List<String> authorIds);

    Flux<Book> findAllByOrderByTitleAsc();

    Flux<Book> findAllBy(Sort sort);

    Flux<Book> findAllBy(Pageable pageable);

    Flux<Book> findAllByIdIn(Iterable<String> ids);

    Mono<Boolean> existsByTitle(String title);

    Mono<Boolean> existsByAuthorIdAndTitle(String authorId, String title);

    Mono<Long> countByAuthorId(String authorId);

    Mono<Long> countByGenreId(String genreId);

    Mono<Void> deleteByAuthorId(String authorId);

    Mono<Void> deleteByGenreId(String genreId);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'authors', localField: 'author.$id', foreignField: '_id', as: 'authorDetails' } }",
            "{ $lookup: { from: 'genres', localField: 'genre.$id', foreignField: '_id', as: 'genreDetails' } }",
            "{ $unwind: '$authorDetails' }",
            "{ $unwind: '$genreDetails' }",
            "{ $project: { " +
                    "id: '$_id', " +
                    "title: 1, " +
                    "author: { id: '$authorDetails._id', fullName: '$authorDetails.fullName' }, " +
                    "genre: { id: '$genreDetails._id', name: '$genreDetails.name' } " +
                    "} }"
    })
    Flux<BookWithDetails> findAllWithDetails();

    /**
     * Агрегация: получить книгу по ID с полными данными
     */
    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { from: 'authors', localField: 'author.$id', foreignField: '_id', as: 'authorDetails' } }",
            "{ $lookup: { from: 'genres', localField: 'genre.$id', foreignField: '_id', as: 'genreDetails' } }",
            "{ $unwind: '$authorDetails' }",
            "{ $unwind: '$genreDetails' }",
            "{ $project: { " +
                    "id: '$_id', " +
                    "title: 1, " +
                    "author: { id: '$authorDetails._id', fullName: '$authorDetails.fullName' }, " +
                    "genre: { id: '$genreDetails._id', name: '$genreDetails.name' } " +
                    "} }"
    })
    Mono<BookWithDetails> findByIdWithDetails(String id);
}
