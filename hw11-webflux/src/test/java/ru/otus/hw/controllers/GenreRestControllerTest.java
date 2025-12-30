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
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(GenreRestController.class)
@AutoConfigureDataMongo
@DisplayName("Тест реактивного контроллера жанров")
class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("Должен возвращать все жанры")
    void shouldReturnAllGenres() {
        // Given
        GenreDto genre1 = new GenreDto();
        genre1.setId("1");
        genre1.setName("Genre 1");

        GenreDto genre2 = new GenreDto();
        genre2.setId("2");
        genre2.setName("Genre 2");

        List<GenreDto> genres = List.of(genre1, genre2);

        when(genreService.findAll()).thenReturn(Flux.fromIterable(genres));

        // When & Then
        webTestClient.get()
                .uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .hasSize(2)
                .contains(genres.get(0), genres.get(1));
    }
}
