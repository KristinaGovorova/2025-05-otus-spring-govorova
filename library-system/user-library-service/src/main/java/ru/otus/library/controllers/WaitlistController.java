package ru.otus.library.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.library.dtos.BookDto;
import ru.otus.library.feign.BookServiceClient;
import ru.otus.library.models.WaitEntry;
import ru.otus.library.repositories.WaitListRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/waitlist")
@RequiredArgsConstructor
public class WaitlistController {

    private final BookServiceClient bookServiceClient;
    private final WaitListRepository waitListRepository;

    @PostMapping
    public ResponseEntity<?> addToWaitList(@RequestParam("userId") String userId,
                                           @RequestParam("bookId") Long bookId) {

        BookDto book = bookServiceClient.getBookById(bookId);
        if (book.getAvailableCopies() > 0) {
            return ResponseEntity.badRequest().body("Book is available, you can borrow it directly!");
        }

        if (waitListRepository.existsByUserIdAndBookId(userId, bookId)) {
            return ResponseEntity.badRequest().body("You are already in the waitlist for this book");
        }

        WaitEntry entry = new WaitEntry();
        entry.setBookId(bookId);
        entry.setUserId(userId);
        entry.setRequestDate(LocalDateTime.now());
        entry.setNotified(false);

        waitListRepository.save(entry);

        return ResponseEntity.ok("You have been added to the waitlist. We will notify you when the book is available.");
    }
}
