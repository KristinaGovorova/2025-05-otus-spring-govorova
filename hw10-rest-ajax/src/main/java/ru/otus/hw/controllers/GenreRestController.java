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
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Validated
public class GenreRestController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping
    public List<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(genreService.findById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/books")
    public List<BookDto> getGenreWithBooks(@PathVariable long id) {
        return bookService.findByGenreId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDto createGenre(@Valid @RequestBody GenreDto genreDto) {
        return genreService.insert(genreDto);
    }

    @PutMapping("/{id}")
    public GenreDto updateGenre(@PathVariable long id, @Valid @RequestBody GenreDto genreDto) {
        genreDto.setId(id);
        return genreService.update(genreDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable long id) {
        genreService.deleteById(id);
    }
}