package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import(BookServiceImpl.class)
@DisplayName("Интеграционный тест для BookServiceImpl")
public class BookServiceImplTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.9");

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        author = authorRepository.save(new Author("Test Author"));
        genre = genreRepository.save(new Genre("Test Genre"));
    }

    @Test
    @DisplayName("должен корректно сохранять книгу")
    void shouldSaveBook() {
        // When
        Book savedBook = bookService.insert("New Book", author.getId(), genre.getId());

        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("New Book");
        assertThat(savedBook.getAuthor()).isNotNull().isEqualTo(author);
        assertThat(savedBook.getGenre()).isNotNull().isEqualTo(genre);
        assertThat(savedBook.getComments()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("должен находить книгу по id")
    void shouldFindById() {
        // Given
        Book book = new Book("Existing Book", author, genre, new ArrayList<>());
        Book expectedBook = bookRepository.save(book);

        // When
        Book actualBook = bookService.findById(expectedBook.getId()).orElse(null);

        // Then
        assertThat(actualBook).isNotNull();
        assertThat(actualBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен находить все книги")
    void shouldFindAllBooks() {
        // Given
        Book book1 = bookRepository.save(new Book("Book 1", author, genre, List.of()));
        Book book2 = bookRepository.save(new Book("Book 2", author, genre, List.of()));

        // When
        List<Book> books = bookService.findAll();

        // Then
        assertThat(books).hasSize(2).containsExactlyInAnyOrder(book1, book2);
    }
}