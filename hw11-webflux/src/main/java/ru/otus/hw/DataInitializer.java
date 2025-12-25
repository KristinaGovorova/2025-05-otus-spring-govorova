package ru.otus.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        clearCollections()
                .then(createAuthors())
                .then(createGenres())
                .then(createBooks())
                .subscribe(
                        result -> log.info("Data initialization completed successfully"),
                        error -> log.error("Error during data initialization", error)
                );
    }

    private Mono<Void> clearCollections() {
        return Mono.when(
                bookRepository.deleteAll(),
                authorRepository.deleteAll(),
                genreRepository.deleteAll()
        ).then(Mono.fromRunnable(() -> log.info("Collections cleared")));
    }

    private Mono<Void> createAuthors() {
        List<Author> authors = Arrays.asList(
                new Author("Лев Толстой"),
                new Author("Фёдор Достоевский"),
                new Author("Александр Пушкин"),
                new Author("Михаил Булгаков"),
                new Author("Антон Чехов")
        );

        return authorRepository.saveAll(authors)
                .collectList()
                .doOnNext(savedAuthors ->
                        log.info("Created {} authors", savedAuthors.size()))
                .then();
    }

    private Mono<Void> createGenres() {
        List<Genre> genres = Arrays.asList(
                new Genre("Роман"),
                new Genre("Повесть"),
                new Genre("Рассказ"),
                new Genre("Драма"),
                new Genre("Поэзия"),
                new Genre("Фантастика")
        );

        return genreRepository.saveAll(genres)
                .collectList()
                .doOnNext(savedGenres ->
                        log.info("Created {} genres", savedGenres.size()))
                .then();
    }

    private Mono<Void> createBooks() {
        return Mono.zip(
                authorRepository.findAll().collectList(),
                genreRepository.findAll().collectList()
        ).flatMap(tuple -> {
            List<Author> authors = tuple.getT1();
            List<Genre> genres = tuple.getT2();

            List<Book> books = Arrays.asList(
                    new Book("Война и мир",
                            authors.stream().filter(a -> a.getFullName().equals("Лев Толстой")).findFirst().orElse(null),
                            genres.stream().filter(g -> g.getName().equals("Роман")).findFirst().orElse(null)),
                    new Book("Преступление и наказание",
                            authors.stream().filter(a -> a.getFullName().equals("Фёдор Достоевский")).findFirst().orElse(null),
                            genres.stream().filter(g -> g.getName().equals("Роман")).findFirst().orElse(null)),
                    new Book("Евгений Онегин",
                            authors.stream().filter(a -> a.getFullName().equals("Александр Пушкин")).findFirst().orElse(null),
                            genres.stream().filter(g -> g.getName().equals("Роман")).findFirst().orElse(null)),
                    new Book("Мастер и Маргарита",
                            authors.stream().filter(a -> a.getFullName().equals("Михаил Булгаков")).findFirst().orElse(null),
                            genres.stream().filter(g -> g.getName().equals("Роман")).findFirst().orElse(null)),
                    new Book("Вишнёвый сад",
                            authors.stream().filter(a -> a.getFullName().equals("Антон Чехов")).findFirst().orElse(null),
                            genres.stream().filter(g -> g.getName().equals("Драма")).findFirst().orElse(null))
            );

            return bookRepository.saveAll(books)
                    .collectList()
                    .doOnNext(savedBooks ->
                            log.info("Created {} books", savedBooks.size()))
                    .then();
        });
    }
}