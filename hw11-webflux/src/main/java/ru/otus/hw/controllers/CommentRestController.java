package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/book/{bookId}")
    public Flux<CommentDto> getCommentsByBook(@PathVariable String bookId) {
        return commentService.findAllByBookId(bookId);
    }

    @GetMapping("/{id}")
    public Mono<CommentDto> getCommentById(@PathVariable String id) {
        return commentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.create(commentDto);
    }

    @PutMapping("/{id}")
    public Mono<CommentDto> updateComment(@PathVariable String id,
                                          @Valid @RequestBody CommentDto commentDto) {
        return commentService.update(id, commentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteComment(@PathVariable String id) {
        return commentService.deleteById(id);
    }

    @DeleteMapping("/book/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCommentsByBook(@PathVariable String bookId) {
        return commentService.deleteAllByBookId(bookId);
    }
}