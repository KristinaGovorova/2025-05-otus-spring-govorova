package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookConverter bookConverter;

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", id)))
                .flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    public Flux<BookDto> findByAuthorId(String authorId) {
        return bookRepository.findByAuthorId(authorId)
                .flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    public Flux<BookDto> findByGenreId(String genreId) {
        return bookRepository.findByGenreId(genreId)
                .flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> create(BookDto bookDto) {
        return Mono.zip(
                        authorRepository.findById(bookDto.getAuthorId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", bookDto.getAuthorId()))),
                        genreRepository.findById(bookDto.getGenreId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", bookDto.getGenreId())))
                ).flatMap(tuple -> {
                    Author author = tuple.getT1();
                    Genre genre = tuple.getT2();

                    Book book = new Book();
                    book.setTitle(bookDto.getTitle());
                    book.setAuthor(author);
                    book.setGenre(genre);

                    return bookRepository.save(book);
                }).flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(String id, BookDto bookDto) {
        return Mono.zip(
                        bookRepository.findById(id)
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", id))),
                        authorRepository.findById(bookDto.getAuthorId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", bookDto.getAuthorId()))),
                        genreRepository.findById(bookDto.getGenreId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", bookDto.getGenreId())))
                ).flatMap(tuple -> {
                    Book book = tuple.getT1();
                    Author author = tuple.getT2();
                    Genre genre = tuple.getT3();

                    book.setTitle(bookDto.getTitle());
                    book.setAuthor(author);
                    book.setGenre(genre);

                    return bookRepository.save(book);
                }).flatMap(this::enrichBookWithDetails)
                .flatMap(bookConverter::toDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", id)))
                .flatMap(book -> bookRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return bookRepository.existsById(id);
    }

    private Mono<Book> enrichBookWithDetails(Book book) {
        if (book.getAuthor() != null && book.getGenre() != null) {
            return Mono.just(book);
        }

        return Mono.zip(
                authorRepository.findById(book.getAuthor().getId()),
                genreRepository.findById(book.getGenre().getId())
        ).map(tuple -> {
            book.setAuthor(tuple.getT1());
            book.setGenre(tuple.getT2());
            return book;
        });
    }
}