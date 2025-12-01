package ru.otus.book.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.book.exceptions.EntityNotFoundException;
import ru.otus.book.models.Book;
import ru.otus.book.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable long id, @RequestBody Book book) {
        book.setId(id);
        return bookService.save(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable long id) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No copies available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookService.save(book);

        return ResponseEntity.ok(book);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable long id) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookService.save(book);
        }

        return ResponseEntity.ok(book);
    }
}