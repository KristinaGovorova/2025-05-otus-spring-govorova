package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return em.createQuery(
                "SELECT c FROM Comment c WHERE c.book.id = :bookId",
                Comment.class
        ).setParameter("bookId", bookId).getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            return insert(comment);
        }
        return update(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        } else {
            throw new EntityNotFoundException("Comment with id %d not found".formatted(id));
        }
    }

    private Comment insert(Comment comment) {
        Book book = em.find(Book.class, comment.getBook().getId());
        if (book == null) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(comment.getBook().getId()));
        }
        comment.setBook(book);

        em.persist(comment);
        return comment;
    }

    private Comment update(Comment comment) {
        Comment existingComment = em.find(Comment.class, comment.getId());
        if (existingComment == null) {
            throw new EntityNotFoundException("Comment with id %d not found".formatted(comment.getId()));
        }

        Book book = em.find(Book.class, comment.getBook().getId());
        if (book == null) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(comment.getBook().getId()));
        }

        existingComment.setText(comment.getText());
        existingComment.setBook(book);

        return em.merge(existingComment);
    }
}