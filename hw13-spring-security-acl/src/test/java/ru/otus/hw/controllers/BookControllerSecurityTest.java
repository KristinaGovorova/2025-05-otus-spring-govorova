package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.AbstractIntegrationTest;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerSecurityTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Администратор может видеть страницу редактирования любой книги")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessEditPage() throws Exception {
        mockMvc.perform(get("/books/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"));
    }

    @Test
    @DisplayName("Пользователь НЕ может редактировать чужую книгу (нет ACL)")
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotEditForeignBook() throws Exception {
        mockMvc.perform(get("/books/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Пользователь может редактировать СВОЮ книгу")
    @WithMockUser(username = "user", roles = {"USER"})
    void userCanEditOwnBook() throws Exception {
        Book myBook = bookService.insert("My Book", 1L, 1L);

        mockMvc.perform(get("/books/edit/" + myBook.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Анонимный пользователь перенаправляется на логин")
    void anonymousRedirectToLogin() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/login"));
    }
}