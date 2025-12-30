package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookDto> getBookById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @GetMapping("/author/{authorId}")
    public Flux<BookDto> getBooksByAuthor(@PathVariable String authorId) {
        return bookService.findByAuthorId(authorId);
    }

    @GetMapping("/genre/{genreId}")
    public Flux<BookDto> getBooksByGenre(@PathVariable String genreId) {
        return bookService.findByGenreId(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        return bookService.create(bookDto);
    }

    @PutMapping("/{id}")
    public Mono<BookDto> updateBook(@PathVariable String id,
                                    @Valid @RequestBody BookDto bookDto) {
        return bookService.update(id, bookDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookService.deleteById(id);
    }
}