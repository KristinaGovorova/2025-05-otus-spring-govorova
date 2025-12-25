package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final CommentConverter commentConverter;

    @Override
    public Flux<CommentDto> findAllByBookId(String bookId) {
        return commentRepository.findByBookIdOrderByCreatedAtDesc(bookId)
                .flatMap(this::enrichCommentWithDetails)
                .map(commentConverter::toDto);
    }

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment", id)))
                .flatMap(this::enrichCommentWithDetails)
                .map(commentConverter::toDto);
    }

    @Override
    public Mono<CommentDto> create(CommentDto commentDto) {
        return bookRepository.findById(commentDto.getBookId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", commentDto.getBookId())))
                .flatMap(book -> {
                    Comment comment = commentConverter.toEntity(commentDto);
                    comment.setBook(book);
                    comment.setCreatedAt(LocalDateTime.now());

                    return commentRepository.save(comment);
                })
                .flatMap(this::enrichCommentWithDetails)
                .map(commentConverter::toDto);
    }

    @Override
    public Mono<CommentDto> update(String id, CommentDto commentDto) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment", id)))
                .flatMap(existingComment -> {
                    existingComment.setText(commentDto.getText());
                    return commentRepository.save(existingComment);
                })
                .flatMap(this::enrichCommentWithDetails)
                .map(commentConverter::toDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment", id)))
                .flatMap(comment -> commentRepository.deleteById(id));
    }

    @Override
    public Mono<Void> deleteAllByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
                .collectList()
                .flatMap(comments -> {
                    if (comments.isEmpty()) {
                        return Mono.empty();
                    }
                    return commentRepository.deleteAll(comments);
                });
    }

    private Mono<Comment> enrichCommentWithDetails(Comment comment) {
        if (comment.getBook() != null && comment.getBook().getTitle() != null) {
            return Mono.just(comment);
        }

        return bookRepository.findById(comment.getBook().getId())
                .map(book -> {
                    comment.setBook(book);
                    return comment;
                });
    }
}