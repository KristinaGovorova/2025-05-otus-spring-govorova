package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .flatMap(this::toDtoWithRelations);
    }

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", id)))
                .flatMap(this::toDtoWithRelations);
    }

    @Override
    public Flux<BookDto> findByAuthorId(String authorId) {
        return bookRepository.findByAuthorId(authorId)
                .flatMap(this::toDtoWithRelations);
    }

    @Override
    public Flux<BookDto> findByGenreId(String genreId) {
        return bookRepository.findByGenreId(genreId)
                .flatMap(this::toDtoWithRelations);
    }

    @Override
    public Mono<BookDto> create(BookDto bookDto) {
        return Mono.zip(
                authorRepository.findById(bookDto.getAuthorId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", bookDto.getAuthorId()))),
                genreRepository.findById(bookDto.getGenreId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", bookDto.getGenreId())))
        ).flatMap(tuple -> {
            Author author = tuple.getT1();
            Genre genre = tuple.getT2();

            Book book = new Book(null, bookDto.getTitle(), author.getId(), genre.getId());

            return bookRepository.save(book)
                    .map(savedBook -> bookConverter.toDto(savedBook, author, genre));
        });
    }

    @Override
    public Mono<BookDto> update(String id, BookDto bookDto) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book", id)))
                .flatMap(book ->
                        Mono.zip(
                                authorRepository.findById(bookDto.getAuthorId())
                                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", bookDto.getAuthorId()))),
                                genreRepository.findById(bookDto.getGenreId())
                                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", bookDto.getGenreId())))
                        ).flatMap(tuple -> {
                            Author author = tuple.getT1();
                            Genre genre = tuple.getT2();

                            book.setTitle(bookDto.getTitle());
                            book.setAuthorId(author.getId());
                            book.setGenreId(genre.getId());

                            return bookRepository.save(book)
                                    .map(savedBook -> bookConverter.toDto(savedBook, author, genre));
                        })
                );
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.deleteById(id);
    }

    private Mono<BookDto> toDtoWithRelations(Book book) {
        return Mono.zip(
                authorRepository.findById(book.getAuthorId())
                        .defaultIfEmpty(new Author("Unknown")),
                genreRepository.findById(book.getGenreId())
                        .defaultIfEmpty(new Genre("Unknown"))
        ).map(tuple -> bookConverter.toDto(book, tuple.getT1(), tuple.getT2()));
    }
}
