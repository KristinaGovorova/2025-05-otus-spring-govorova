package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.author " +
            "LEFT JOIN FETCH b.genre " +
            "LEFT JOIN FETCH b.comments " +
            "WHERE b.id = :id")
    Optional<Book> findByIdWithComments(@Param("id") long id);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.author " +
            "LEFT JOIN FETCH b.genre " +
            "LEFT JOIN FETCH b.comments")
    List<Book> findAllWithComments();
}