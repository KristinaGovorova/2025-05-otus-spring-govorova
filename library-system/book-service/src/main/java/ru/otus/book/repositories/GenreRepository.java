package ru.otus.book.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.book.models.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}