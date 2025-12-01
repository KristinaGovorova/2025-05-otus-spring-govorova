package ru.otus.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.library.models.Loan;
import ru.otus.library.models.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByUserIdAndBookIdAndStatus(String userId, Long bookId, LoanStatus status);

    boolean existsByUserIdAndBookId(String userId, Long bookId);

    List<Loan> findAllByUserId(String userId);
}