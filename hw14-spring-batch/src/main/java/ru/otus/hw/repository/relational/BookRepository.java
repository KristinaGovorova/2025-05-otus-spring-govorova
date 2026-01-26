package ru.otus.hw.repository.relational;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.relational.BookEntity;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    @EntityGraph(attributePaths = {"author", "genre"})
    List<BookEntity> findAll();
}