package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами ")
@JdbcTest
@Import(JdbcGenreRepository.class)
public class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository genreJdbc;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> genres = genreJdbc.findAll();

        assertThat(genres).hasSize(3);

        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getName()).isEqualTo("Genre_1");

        assertThat(genres.get(1).getId()).isEqualTo(2);
        assertThat(genres.get(1).getName()).isEqualTo("Genre_2");

        assertThat(genres.get(2).getId()).isEqualTo(3);
        assertThat(genres.get(2).getName()).isEqualTo("Genre_3");
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        Optional<Genre> genre = genreJdbc.findById(1L);

        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1);
        assertThat(genre.get().getName()).isEqualTo("Genre_1");
    }

    @DisplayName("должен возвращать пустой Optional для несуществующего жанра")
    @Test
    void shouldReturnEmptyOptionalForNonExistingGenre() {
        Optional<Genre> genre = genreJdbc.findById(99L);
        assertThat(genre).isEmpty();
    }

    @DisplayName("должен возвращать пустой Optional для отрицательного жанра")
    @Test
    void shouldReturnEmptyOptionalForNegativeId() {
        Optional<Genre> genre = genreJdbc.findById(-1L);
        assertThat(genre).isEmpty();
    }
}
