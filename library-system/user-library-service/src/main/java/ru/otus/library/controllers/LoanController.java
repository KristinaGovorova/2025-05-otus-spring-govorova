package ru.otus.library.controllers;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.library.feign.BookServiceClient;
import ru.otus.library.models.Loan;
import ru.otus.library.models.LoanStatus;
import ru.otus.library.models.WaitEntry;
import ru.otus.library.repositories.LoanRepository;
import ru.otus.library.repositories.WaitListRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanRepository loanRepository;
    private final WaitListRepository waitListRepository;
    private final BookServiceClient bookServiceClient;

    @GetMapping
    public List<Loan> getLoans(@RequestParam(name = "userId", required = false) String userId) {
        if (userId != null && !userId.isBlank()) {
            return loanRepository.findAllByUserId(userId);
        } else {
            return loanRepository.findAll();
        }
    }

    @PostMapping
    @CircuitBreaker(name = "bookServiceBreaker", fallbackMethod = "borrowFallback")
    public ResponseEntity<?> createLoan(@RequestParam("userId") String userId,
                                        @RequestParam("bookId") Long bookId) {

        if (loanRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ACTIVE)) {
            return ResponseEntity.badRequest().body("You already have this book");
        }

        try {
            bookServiceClient.borrowBook(bookId);

            Loan loan = new Loan();
            loan.setBookId(bookId);
            loan.setUserId(userId);
            loan.setIssueDate(LocalDate.now());
            loan.setReturnDeadline(LocalDate.now().plusWeeks(2));
            loan.setStatus(LoanStatus.ACTIVE);

            loanRepository.save(loan);
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);

        } catch (FeignException.Conflict e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No copies available");
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    public ResponseEntity<?> borrowFallback(String userId, Long bookId, Throwable t) {
        System.err.println("Circuit Breaker triggered: " + t.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Каталог книг временно недоступен. Попробуйте позже или встаньте в очередь.");
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable("id") Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            return ResponseEntity.badRequest().body("Loan already returned");
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setActualReturnDate(LocalDate.now());
        loanRepository.save(loan);

        try {
            bookServiceClient.returnBook(loan.getBookId());
        } catch (Exception e) {
        }

        var waiterOpt = waitListRepository.findFirstByBookIdAndNotifiedFalseOrderByRequestDateAsc(loan.getBookId());
        if (waiterOpt.isPresent()) {
            WaitEntry waiter = waiterOpt.get();
            waiter.setNotified(true);
            waitListRepository.save(waiter);
            return ResponseEntity.ok("Returned. User " + waiter.getUserId() + " notified.");
        }

        return ResponseEntity.ok("Returned successfully");
    }
}