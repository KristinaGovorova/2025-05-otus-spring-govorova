package ru.otus.book.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.book.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findAll();

    List<Book> findByAuthorId(long authorId);

    List<Book> findByGenreId(long genreId);
}