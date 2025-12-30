package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(AuthorRestController.class)
@AutoConfigureDataMongo
@DisplayName("Тест реактивного контроллера авторов")
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("Должен возвращать всех авторов")
    void shouldReturnAllAuthors() {
        // Given
        AuthorDto author1 = new AuthorDto();
        author1.setId("1");
        author1.setFullName("Author 1");

        AuthorDto author2 = new AuthorDto();
        author2.setId("2");
        author2.setFullName("Author 2");

        List<AuthorDto> authors = List.of(author1, author2);

        when(authorService.findAll()).thenReturn(Flux.fromIterable(authors));

        // When & Then
        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(2)
                .contains(author1, author2);
    }
}
