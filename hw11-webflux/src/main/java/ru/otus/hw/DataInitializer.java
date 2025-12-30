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
    public void run(String... args) {
        log.info("Starting data initialization...");

        clearCollections()
                .then(createAuthors())
                .then(createGenres())
                .then(createBooks())
                .doOnSuccess(v -> log.info("Data initialization completed successfully"))
                .doOnError(e -> log.error("Error during data initialization", e))
                .block();
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
                new Author(null, "Лев Толстой"),
                new Author(null, "Фёдор Достоевский"),
                new Author(null, "Александр Пушкин"),
                new Author(null, "Михаил Булгаков"),
                new Author(null, "Антон Чехов")
        );
        return authorRepository.saveAll(authors)
                .doOnNext(a -> log.info("Author created: {}", a.getFullName()))
                .then();
    }

    private Mono<Void> createGenres() {
        List<Genre> genres = Arrays.asList(
                new Genre(null, "Роман"),
                new Genre(null, "Повесть"),
                new Genre(null, "Рассказ"),
                new Genre(null, "Драма"),
                new Genre(null, "Поэзия"),
                new Genre(null, "Фантастика")
        );
        return genreRepository.saveAll(genres)
                .doOnNext(g -> log.info("Genre created: {}", g.getName()))
                .then();
    }

    private Mono<Void> createBooks() {
        return Mono.zip(
                authorRepository.findAll().collectList(),
                genreRepository.findAll().collectList()
        ).flatMap(tuple -> {
            List<Author> authors = tuple.getT1();
            List<Genre> genres = tuple.getT2();

            if (authors.isEmpty() || genres.isEmpty()) {
                return Mono.empty();
            }

            Author tolstoy = findAuthor(authors, "Толстой");
            Author dostoevsky = findAuthor(authors, "Достоевский");
            Author pushkin = findAuthor(authors, "Пушкин");
            Author bulgakov = findAuthor(authors, "Булгаков");
            Author chekhov = findAuthor(authors, "Чехов");

            Genre novel = findGenre(genres, "Роман");
            Genre drama = findGenre(genres, "Драма");

            List<Book> books = Arrays.asList(
                    new Book(null, "Война и мир", tolstoy.getId(), novel.getId()),
                    new Book(null, "Преступление и наказание", dostoevsky.getId(), novel.getId()),
                    new Book(null, "Евгений Онегин", pushkin.getId(), novel.getId()),
                    new Book(null, "Мастер и Маргарита", bulgakov.getId(), novel.getId()),
                    new Book(null, "Вишнёвый сад", chekhov.getId(), drama.getId())
            );

            return bookRepository.saveAll(books)
                    .doOnNext(b -> log.info("Book created: {}", b.getTitle()))
                    .then();
        });
    }

    private Author findAuthor(List<Author> authors, String namePart) {
        return authors.stream()
                .filter(a -> a.getFullName().contains(namePart))
                .findFirst()
                .orElse(authors.get(0));
    }

    private Genre findGenre(List<Genre> genres, String name) {
        return genres.stream()
                .filter(g -> g.getName().equals(name))
                .findFirst()
                .orElse(genres.get(0));
    }
}
