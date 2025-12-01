package ru.otus.book.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.book.exceptions.EntityNotFoundException;
import ru.otus.book.models.Book;
import ru.otus.book.repositories.AuthorRepository;
import ru.otus.book.repositories.BookRepository;
import ru.otus.book.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByGenreId(long genreId) {
        return bookRepository.findByGenreId(genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book save(Book book) {
        if (book.getId() == null) {
            return create(book);
        } else {
            return update(book);
        }
    }

    private Book create(Book book) {
        var author = authorRepository.findById(book.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        var genre = genreRepository.findById(book.getGenre().getId())
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        book.setAuthor(author);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

    private Book update(Book book) {
        var existing = bookRepository.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        existing.setTitle(book.getTitle());
        existing.setAuthor(authorRepository.findById(book.getAuthor().getId()).orElseThrow());
        existing.setGenre(genreRepository.findById(book.getGenre().getId()).orElseThrow());

        return bookRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
        bookRepository.deleteById(id);
    }
}