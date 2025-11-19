package ru.otus.hw.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @Test
    @DisplayName("Должен возвращать список всех книг (GET /books)")
    void shouldReturnAllBooks() throws Exception {
        // Given
        Book book = new Book(1L, "Test Book", new Author(1L, "Author"), new Genre(1L, "Genre"));
        given(bookService.findAll()).willReturn(List.of(book));

        // When & Then
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(book)));
    }

    @Test
    @DisplayName("Должен возвращать страницу просмотра книги (GET /books/{id})")
    void shouldReturnBookView() throws Exception {
        // Given
        long bookId = 1L;
        Book book = new Book(bookId, "Test Book", new Author(1L, "Author"), new Genre(1L, "Genre"));
        given(bookService.findById(bookId)).willReturn(Optional.of(book));

        // When & Then
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/view"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", book));
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если книга не найдена (GET /books/{id})")
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        long bookId = 99L;
        given(bookService.findById(bookId)).willReturn(Optional.empty());

        // When & Then
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/books/{id}", bookId));
        });

        Assertions.assertInstanceOf(EntityNotFoundException.class, exception.getCause());
    }

    @Test
    @DisplayName("Должен возвращать форму создания книги (GET /books/create)")
    void shouldReturnCreateBookForm() throws Exception {
        // Given
        given(authorService.findAll()).willReturn(List.of(new Author(1L, "Author")));
        given(genreService.findAll()).willReturn(List.of(new Genre(1L, "Genre")));

        // When & Then
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @DisplayName("Должен создавать новую книгу и редиректить (POST /books/create)")
    void shouldCreateBook() throws Exception {
        // Given
        String title = "New Book";
        long authorId = 1L;
        long genreId = 2L;

        // When & Then
        mockMvc.perform(post("/books/create")
                        .param("title", title)
                        .param("authorId", String.valueOf(authorId))
                        .param("genreId", String.valueOf(genreId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).insert(title, authorId, genreId);
    }

    @Test
    @DisplayName("Должен редиректить на ошибку при сбое создания (POST /books/create)")
    void shouldRedirectToErrorOnCreateFail() throws Exception {
        // Given
        doThrow(new RuntimeException("Service Error")).when(bookService).insert(anyString(), anyLong(), anyLong());

        // When & Then
        mockMvc.perform(post("/books/create")
                        .param("title", "Fail Book")
                        .param("authorId", "1")
                        .param("genreId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/create?error"));
    }

    @Test
    @DisplayName("Должен возвращать форму редактирования (GET /books/edit/{id})")
    void shouldReturnEditBookForm() throws Exception {
        // Given
        long bookId = 1L;
        Book book = new Book(bookId, "Edit Book", new Author(1L, "A"), new Genre(1L, "G"));

        given(bookService.findById(bookId)).willReturn(Optional.of(book));
        given(authorService.findAll()).willReturn(List.of());
        given(genreService.findAll()).willReturn(List.of());

        // When & Then
        mockMvc.perform(get("/books/edit/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attribute("book", book));
    }

    @Test
    @DisplayName("Должен обновлять книгу и редиректить (POST /books/edit/{id})")
    void shouldUpdateBook() throws Exception {
        // Given
        long bookId = 1L;
        String newTitle = "Updated Title";
        long authorId = 2L;
        long genreId = 3L;

        // When & Then
        mockMvc.perform(post("/books/edit/{id}", bookId)
                        .param("title", newTitle)
                        .param("authorId", String.valueOf(authorId))
                        .param("genreId", String.valueOf(genreId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).update(bookId, newTitle, authorId, genreId);
    }

    @Test
    @DisplayName("Должен возвращать страницу подтверждения удаления (GET /books/delete-confirm/{id})")
    void shouldReturnDeleteConfirmPage() throws Exception {
        // Given
        long bookId = 1L;
        Book book = new Book(bookId, "Delete Me", new Author(1L, "A"), new Genre(1L, "G"));
        given(bookService.findById(bookId)).willReturn(Optional.of(book));

        // When & Then
        mockMvc.perform(get("/books/delete-confirm/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/delete-confirm"))
                .andExpect(model().attribute("book", book));
    }

    @Test
    @DisplayName("Должен удалять книгу и редиректить (POST /books/delete/{id})")
    void shouldDeleteBook() throws Exception {
        // Given
        long bookId = 1L;

        // When & Then
        mockMvc.perform(post("/books/delete/{id}", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById(bookId);
    }
}