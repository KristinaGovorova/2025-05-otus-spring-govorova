package ru.otus.hw.services;


import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    @Transactional(readOnly = true)
    Optional<Comment> findById(String bookId, String commentId);

    @Transactional(readOnly = true)
    List<Comment> findByBookId(String bookId);

    @Transactional
    Comment insert(String bookId, String text);

    @Transactional
    Comment update(String bookId, String commentId, String text);

    @Transactional
    void deleteById(String bookId, String commentId);
}