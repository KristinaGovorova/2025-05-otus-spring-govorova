package ru.otus.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.library.models.WaitEntry;

import java.util.Optional;

public interface WaitListRepository extends JpaRepository<WaitEntry, Long> {
    Optional<WaitEntry> findFirstByBookIdAndNotifiedFalseOrderByRequestDateAsc(Long bookId);

    boolean existsByUserIdAndBookId(String userId, Long bookId);
}