package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findById(String bookId, String commentId) {
        return bookRepository.findById(bookId)
                .flatMap(book -> book.getComments().stream()
                        .filter(c -> c.getId().equals(commentId))
                        .findFirst());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(String bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getComments)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
    }

    @Transactional
    @Override
    public Comment insert(String bookId, String text) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        Comment comment = new Comment(text);

        book.getComments().add(comment);
        bookRepository.save(book);

        return comment;
    }

    @Transactional
    @Override
    public Comment update(String bookId, String commentId, String text) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        Comment commentToUpdate = book.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found in book %s"
                        .formatted(commentId, bookId)));

        commentToUpdate.setText(text);
        bookRepository.save(book);

        return commentToUpdate;
    }

    @Transactional
    @Override
    public void deleteById(String bookId, String commentId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        boolean removed = book.getComments().removeIf(c -> c.getId().equals(commentId));

        if (!removed) {
            throw new EntityNotFoundException("Comment with id %s not found in book %s".formatted(commentId, bookId));
        }

        bookRepository.save(book);
    }
}