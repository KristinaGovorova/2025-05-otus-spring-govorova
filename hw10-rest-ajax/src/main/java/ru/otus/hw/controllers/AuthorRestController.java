package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Validated
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(authorService.findById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/books")
    public List<AuthorDto> getAuthorWithBooks(@PathVariable long id) {
        return authorService.findAll(); // Здесь можно вернуть автора с книгами, если нужно
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        return authorService.insert(authorDto);
    }

    @PutMapping("/{id}")
    public AuthorDto updateAuthor(@PathVariable long id, @Valid @RequestBody AuthorDto authorDto) {
        authorDto.setId(id);
        return authorService.update(authorDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable long id) {
        authorService.deleteById(id);
    }
}