package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        List<Book> books = em.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "LEFT JOIN FETCH b.author " +
                                "LEFT JOIN FETCH b.genre " +
                                "LEFT JOIN FETCH b.comments " +
                                "WHERE b.id = :id", Book.class)
                .setParameter("id", id)
                .getResultList();

        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery(
                "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.author " +
                        "LEFT JOIN FETCH b.genre " +
                        "LEFT JOIN FETCH b.comments",
                Book.class
        ).getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        } else {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
    }

    private Book insert(Book book) {
        Author author = em.find(Author.class, book.getAuthor().getId());
        if (author == null) {
            throw new EntityNotFoundException("Author with id %d not found".formatted(book.getAuthor().getId()));
        }
        book.setAuthor(author);

        Genre genre = em.find(Genre.class, book.getGenre().getId());
        if (genre == null) {
            throw new EntityNotFoundException("Genre with id %d not found".formatted(book.getGenre().getId()));
        }
        book.setGenre(genre);

        em.persist(book);
        return book;
    }

    private Book update(Book book) {
        Book existingBook = em.find(Book.class, book.getId());
        if (existingBook == null) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }

        Author author = em.find(Author.class, book.getAuthor().getId());
        if (author == null) {
            throw new EntityNotFoundException("Author with id %d not found".formatted(book.getAuthor().getId()));
        }

        Genre genre = em.find(Genre.class, book.getGenre().getId());
        if (genre == null) {
            throw new EntityNotFoundException("Genre with id %d not found".formatted(book.getGenre().getId()));
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(author);
        existingBook.setGenre(genre);

        return em.merge(existingBook);
    }
}