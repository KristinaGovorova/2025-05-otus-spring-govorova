package ru.otus.book.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.book.models.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}