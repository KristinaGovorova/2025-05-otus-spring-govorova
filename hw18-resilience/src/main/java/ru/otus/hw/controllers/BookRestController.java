package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.ExternalDataService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookRestController {

    private final BookService bookService;
    private final ExternalDataService externalDataService;

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable long id) {
        try {
            BookDto book = bookService.findById(id);

            String info = externalDataService.getAdditionalInfo(id);
            book.setExternalInfo(info);

            return ResponseEntity.ok(book);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/authors/{authorId}")
    public List<BookDto> getBooksByAuthor(@PathVariable long authorId) {
        return bookService.findByAuthorId(authorId);
    }

    @GetMapping("/genres/{genreId}")
    public List<BookDto> getBooksByGenre(@PathVariable long genreId) {
        return bookService.findByGenreId(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody BookDto bookDto) {
        return bookService.insert(bookDto);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable long id, @Valid @RequestBody BookDto bookDto) {
        bookDto.setId(id);
        return bookService.update(bookDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"message\":\"" + e.getMessage() + "\"}");
    }
}