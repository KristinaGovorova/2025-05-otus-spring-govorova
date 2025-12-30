package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с жанрами")
@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

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
        genreRepository.saveAll(genres);
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
        genre = genreRepository.save(genre);
        em.clear();

        Optional<Genre> foundGenre = genreRepository.findById(genre.getId());

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

    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var newGenre = new Genre(null, "New_Genre");
        var savedGenre = genreRepository.save(newGenre);

        assertThat(savedGenre).isNotNull();
        assertThat(savedGenre.getId()).isPositive();
        assertThat(savedGenre.getName()).isEqualTo("New_Genre");

        var foundGenre = genreRepository.findById(savedGenre.getId());
        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo("New_Genre");
    }

    @DisplayName("должен обновлять существующий жанр")
    @Test
    void shouldUpdateGenre() {
        var genre = new Genre(null, "Original_Name");
        genre = genreRepository.save(genre);

        genre.setName("Updated_Name");
        var updatedGenre = genreRepository.save(genre);

        assertThat(updatedGenre.getName()).isEqualTo("Updated_Name");

        var foundGenre = genreRepository.findById(genre.getId());
        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo("Updated_Name");
    }

    @DisplayName("должен удалять жанр по id")
    @Test
    void shouldDeleteGenre() {
        var genre = new Genre(null, "Genre_To_Delete");
        genre = genreRepository.save(genre);
        var genreId = genre.getId();

        assertThat(genreRepository.findById(genreId)).isPresent();

        genreRepository.deleteById(genreId);

        assertThat(genreRepository.findById(genreId)).isEmpty();
    }

    @Test
    @DisplayName("должен проверять существование жанра")
    void shouldCheckGenreExistence() {
        var genre = new Genre(null, "Test_Genre");
        genre = genreRepository.save(genre);

        assertThat(genreRepository.existsById(genre.getId())).isTrue();
        assertThat(genreRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("должен возвращать количество жанров")
    void shouldReturnGenresCount() {
        genreRepository.deleteAll();

        var genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2")
        );
        genreRepository.saveAll(genres);

        assertThat(genreRepository.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("должен удалять все жанры")
    void shouldDeleteAllGenres() {
        var genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2")
        );
        genreRepository.saveAll(genres);

        assertThat(genreRepository.count()).isEqualTo(2L);

        genreRepository.deleteAll();

        assertThat(genreRepository.count()).isZero();
    }
}