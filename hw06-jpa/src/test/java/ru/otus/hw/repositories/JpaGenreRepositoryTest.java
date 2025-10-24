package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами ")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3")
        );
        genres.forEach(em::persist);
        em.flush();
        em.clear();

        List<Genre> foundGenres = genreRepository.findAll();

        assertThat(foundGenres).hasSize(3);
        assertThat(foundGenres)
                .extracting(Genre::getName)
                .containsExactly("Genre_1", "Genre_2", "Genre_3");
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var genre = new Genre(null, "Test_Genre");
        em.persist(genre);
        em.flush();
        em.clear();

        Long savedId = genre.getId();

        Optional<Genre> foundGenre = genreRepository.findById(savedId);

        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo("Test_Genre");
    }

    @DisplayName("должен возвращать пустой Optional для несуществующего жанра")
    @Test
    void shouldReturnEmptyOptionalForNonExistingGenre() {
        Optional<Genre> genre = genreRepository.findById(99L);
        assertThat(genre).isEmpty();
    }

    @DisplayName("должен возвращать пустой Optional для отрицательного id")
    @Test
    void shouldReturnEmptyOptionalForNegativeId() {
        Optional<Genre> genre = genreRepository.findById(-1L);
        assertThat(genre).isEmpty();
    }
}