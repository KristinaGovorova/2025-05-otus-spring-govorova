package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        return bookConverter.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId)
                .stream()
                .map(bookConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findByGenreId(long genreId) {
        return bookRepository.findByGenreId(genreId)
                .stream()
                .map(bookConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDto insert(String title, long authorId, long genreId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));

        Book book = new Book(title, author, genre);
        Book savedBook = bookRepository.save(book);
        return bookConverter.toDto(savedBook);
    }

    @Override
    @Transactional
    public BookDto insert(BookDto bookDto) {
        return insert(bookDto.getTitle(), bookDto.getAuthorId(), bookDto.getGenreId());
    }

    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, long genreId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        Book updatedBook = bookRepository.save(book);
        return bookConverter.toDto(updatedBook);
    }

    @Override
    @Transactional
    public BookDto update(BookDto bookDto) {
        return update(bookDto.getId(), bookDto.getTitle(), bookDto.getAuthorId(), bookDto.getGenreId());
    }

    @Override
    @Transactional
    public BookDto save(Book book) {
        if (book.getId() == null || book.getId() == 0) {
            return insert(book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        } else {
            return update(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        }
    }

    @Override
    @Transactional
    public BookDto save(BookDto bookDto) {
        if (bookDto.getId() == null || bookDto.getId() == 0) {
            return insert(bookDto);
        } else {
            return update(bookDto);
        }
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