package ru.otus.library.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.library.models.Review;
import ru.otus.library.repositories.LoanRepository;
import ru.otus.library.repositories.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final LoanRepository loanRepository;

    @GetMapping
    public List<Review> getBookReviews(@RequestParam("bookId") Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestParam("userId") String userId,
                                       @RequestParam("bookId") Long bookId,
                                       @RequestParam("rating") int rating,
                                       @RequestParam("text") String text) {

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        boolean hasBorrowed = loanRepository.existsByUserIdAndBookId(userId, bookId);
        if (!hasBorrowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only review books you have borrowed.");
        }

        if (reviewRepository.existsByUserIdAndBookId(userId, bookId)) {
            return ResponseEntity.badRequest().body("You have already reviewed this book");
        }

        Review review = new Review();
        review.setBookId(bookId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setText(text);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return ResponseEntity.ok(review);
    }
}