package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BookRestController.class)
@AutoConfigureDataMongo
@DisplayName("Тест реактивного REST-контроллера книг")
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("Должен возвращать все книги")
    void shouldReturnAllBooks() {
        // Given
        List<BookDto> books = List.of(
                createBookDto("1", "Book 1", "1", "Author 1", "1", "Genre 1"),
                createBookDto("2", "Book 2", "2", "Author 2", "2", "Genre 2")
        );

        when(bookService.findAll()).thenReturn(Flux.fromIterable(books));

        // When & Then
        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .hasSize(2)
                .contains(books.get(0), books.get(1));
    }

    @Test
    @DisplayName("Должен возвращать книгу по ID")
    void shouldReturnBookById() {
        // Given
        BookDto book = createBookDto("1", "Book 1", "1", "Author 1", "1", "Genre 1");

        when(bookService.findById("1")).thenReturn(Mono.just(book));

        // When & Then
        webTestClient.get()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(book);
    }

    @Test
    @DisplayName("Должен создавать новую книгу")
    void shouldCreateNewBook() {
        // Given
        BookDto requestDto = new BookDto();
        requestDto.setTitle("New Book");
        requestDto.setAuthorId("1");
        requestDto.setGenreId("1");

        BookDto responseDto = createBookDto("1", "New Book", "1", "Author 1", "1", "Genre 1");

        when(bookService.create(any(BookDto.class))).thenReturn(Mono.just(responseDto));

        // When & Then
        webTestClient.post()
                .uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Должен обновлять существующую книгу")
    void shouldUpdateBook() {
        // Given
        BookDto requestDto = new BookDto();
        requestDto.setTitle("Updated Book");
        requestDto.setAuthorId("2");
        requestDto.setGenreId("2");

        BookDto responseDto = createBookDto("1", "Updated Book", "2", "Author 2", "2", "Genre 2");

        when(bookService.update(eq("1"), any(BookDto.class))).thenReturn(Mono.just(responseDto));

        // When & Then
        webTestClient.put()
                .uri("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Должен удалять книгу")
    void shouldDeleteBook() {
        // Given
        when(bookService.deleteById("1")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Должен возвращать книги по автору")
    void shouldReturnBooksByAuthor() {
        // Given
        List<BookDto> books = List.of(
                createBookDto("1", "Book 1", "1", "Author 1", "1", "Genre 1"),
                createBookDto("2", "Book 2", "1", "Author 1", "2", "Genre 2")
        );

        when(bookService.findByAuthorId("1")).thenReturn(Flux.fromIterable(books));

        // When & Then
        webTestClient.get()
                .uri("/api/books/author/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Должен возвращать книги по жанру")
    void shouldReturnBooksByGenre() {
        // Given
        List<BookDto> books = List.of(
                createBookDto("1", "Book 1", "1", "Author 1", "1", "Genre 1"),
                createBookDto("2", "Book 2", "2", "Author 2", "1", "Genre 1")
        );

        when(bookService.findByGenreId("1")).thenReturn(Flux.fromIterable(books));

        // When & Then
        webTestClient.get()
                .uri("/api/books/genre/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(2);
    }

    private BookDto createBookDto(String id, String title, String authorId, String authorName,
                                  String genreId, String genreName) {
        BookDto dto = new BookDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthorId(authorId);
        dto.setAuthorName(authorName);
        dto.setGenreId(genreId);
        dto.setGenreName(genreName);
        return dto;
    }
}