package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public Flux<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<GenreDto> getGenreById(@PathVariable String id) {
        return genreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GenreDto> createGenre(@Valid @RequestBody GenreDto genreDto) {
        return genreService.create(genreDto);
    }

    @PutMapping("/{id}")
    public Mono<GenreDto> updateGenre(@PathVariable String id,
                                      @Valid @RequestBody GenreDto genreDto) {
        return genreService.update(id, genreDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGenre(@PathVariable String id) {
        return genreService.deleteById(id);
    }
}