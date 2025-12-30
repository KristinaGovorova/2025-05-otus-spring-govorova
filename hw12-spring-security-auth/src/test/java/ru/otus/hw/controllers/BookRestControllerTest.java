package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@DisplayName("Тест REST-контроллера книг")
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен возвращать все книги")
    void shouldReturnAllBooks() throws Exception {
        // Given
        List<BookDto> books = List.of(
                createBookDto(1L, "Book 1", 1L, "Author 1", 1L, "Genre 1"),
                createBookDto(2L, "Book 2", 2L, "Author 2", 2L, "Genre 2")
        );

        when(bookService.findAll()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Book 1")));
    }

    @Test
    @DisplayName("Должен возвращать пустой список если книг нет")
    void shouldReturnEmptyListWhenNoBooks() throws Exception {
        // Given
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Должен возвращать книгу по ID")
    void shouldReturnBookById() throws Exception {
        // Given
        BookDto book = createBookDto(1L, "Book 1", 1L, "Author 1", 1L, "Genre 1");
        when(bookService.findById(1L)).thenReturn(book);

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Book 1")));
    }

    @Test
    @DisplayName("Должен возвращать 404 если книга не найдена")
    void shouldReturn404WhenBookNotFound() throws Exception {
        // Given
        when(bookService.findById(999L))
                .thenThrow(new EntityNotFoundException("Book with id 999 not found"));

        // When & Then
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("Должен создавать новую книгу")
    void shouldCreateNewBook() throws Exception {
        // Given
        BookDto requestDto = new BookDto();
        requestDto.setTitle("New Book");
        requestDto.setAuthorId(1L);
        requestDto.setGenreId(1L);

        BookDto responseDto = createBookDto(1L, "New Book", 1L, "Author 1", 1L, "Genre 1");
        when(bookService.insert(any(BookDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Book")));

        verify(bookService).insert(any(BookDto.class));
    }

    @Test
    @DisplayName("Должен возвращать 400 при создании книги без названия")
    void shouldReturn400WhenCreateBookWithoutTitle() throws Exception {
        // Given
        BookDto invalidDto = new BookDto();
        invalidDto.setAuthorId(1L);
        invalidDto.setGenreId(1L);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).insert(any());
    }

    @Test
    @DisplayName("Должен возвращать 400 при создании книги без автора")
    void shouldReturn400WhenCreateBookWithoutAuthor() throws Exception {
        // Given
        BookDto invalidDto = new BookDto();
        invalidDto.setTitle("Test Book");
        invalidDto.setGenreId(1L);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).insert(any());
    }

    @Test
    @DisplayName("Должен обновлять существующую книгу")
    void shouldUpdateBook() throws Exception {
        // Given
        BookDto requestDto = new BookDto();
        requestDto.setTitle("Updated Book");
        requestDto.setAuthorId(2L);
        requestDto.setGenreId(2L);

        BookDto responseDto = createBookDto(1L, "Updated Book", 2L, "Author 2", 2L, "Genre 2");
        when(bookService.update(any(BookDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Book")));

        verify(bookService).update(any(BookDto.class));
    }

    @Test
    @DisplayName("Должен удалять книгу")
    void shouldDeleteBook() throws Exception {
        // Given
        doNothing().when(bookService).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(1L);
    }

    @Test
    @DisplayName("Должен возвращать книги по автору")
    void shouldReturnBooksByAuthor() throws Exception {
        // Given
        List<BookDto> books = List.of(
                createBookDto(1L, "Book 1", 1L, "Author 1", 1L, "Genre 1"),
                createBookDto(2L, "Book 2", 1L, "Author 1", 2L, "Genre 2")
        );

        when(bookService.findByAuthorId(1L)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/author/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].authorId", is(1)));
    }

    @Test
    @DisplayName("Должен возвращать книги по жанру")
    void shouldReturnBooksByGenre() throws Exception {
        // Given
        List<BookDto> books = List.of(
                createBookDto(1L, "Book 1", 1L, "Author 1", 1L, "Genre 1"),
                createBookDto(2L, "Book 2", 2L, "Author 2", 1L, "Genre 1")
        );

        when(bookService.findByGenreId(1L)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/genre/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].genreId", is(1)));
    }

    @Test
    @DisplayName("Должен возвращать пустой список если нет книг по автору")
    void shouldReturnEmptyListWhenNoBooksByAuthor() throws Exception {
        // Given
        when(bookService.findByAuthorId(999L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/books/author/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Должен возвращать пустой список если нет книг по жанру")
    void shouldReturnEmptyListWhenNoBooksByGenre() throws Exception {
        // Given
        when(bookService.findByGenreId(999L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/books/genre/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private BookDto createBookDto(Long id, String title, Long authorId, String authorName,
                                  Long genreId, String genreName) {
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