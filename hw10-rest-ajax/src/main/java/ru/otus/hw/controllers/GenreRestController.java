package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Validated
public class GenreRestController {

    private final GenreService genreService;

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
    public List<GenreDto> getGenreWithBooks(@PathVariable long id) {
        return genreService.findAll(); // Здесь можно вернуть жанр с книгами, если нужно
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