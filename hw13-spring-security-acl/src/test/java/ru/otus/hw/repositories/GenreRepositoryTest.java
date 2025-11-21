package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.AbstractIntegrationTest;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с жанрами")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GenreRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;

    private static final long EXISTING_GENRE_ID = 1L;
    private static final String EXISTING_GENRE_NAME = "Fantasy";

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var foundGenres = genreRepository.findAll();

        assertThat(foundGenres).hasSize(3);
        assertThat(foundGenres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Fantasy", "Sci-Fi", "Non-Fiction");
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var foundGenre = genreRepository.findById(EXISTING_GENRE_ID);

        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo(EXISTING_GENRE_NAME);
    }

    @DisplayName("должен возвращать пустой Optional для несуществующего жанра")
    @Test
    void shouldReturnEmptyOptionalForNonExistingGenre() {
        var genre = genreRepository.findById(9999L);
        assertThat(genre).isEmpty();
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var newGenre = new Genre(null, "New_Genre");
        var savedGenre = genreRepository.save(newGenre);

        assertThat(savedGenre).isNotNull();
        assertThat(savedGenre.getId()).isGreaterThan(0);

        var foundGenre = em.find(Genre.class, savedGenre.getId());
        assertThat(foundGenre.getName()).isEqualTo("New_Genre");
    }

    @DisplayName("должен обновлять существующий жанр")
    @Test
    void shouldUpdateGenre() {
        var genre = em.find(Genre.class, EXISTING_GENRE_ID);

        genre.setName("Updated_Name");
        genreRepository.save(genre);
        em.flush();
        em.clear();

        var foundGenre = em.find(Genre.class, EXISTING_GENRE_ID);
        assertThat(foundGenre.getName()).isEqualTo("Updated_Name");
    }

    @DisplayName("должен удалять жанр по id")
    @Test
    void shouldDeleteGenre() {
        var tempGenre = new Genre(null, "Genre_To_Delete");
        em.persist(tempGenre);
        em.flush();
        var id = tempGenre.getId();

        genreRepository.deleteById(id);
        em.flush();
        em.clear();

        assertThat(genreRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("должен проверять существование жанра")
    void shouldCheckGenreExistence() {
        assertThat(genreRepository.existsById(EXISTING_GENRE_ID)).isTrue();
        assertThat(genreRepository.existsById(9999L)).isFalse();
    }

    @Test
    @DisplayName("должен возвращать количество жанров")
    void shouldReturnGenresCount() {
        assertThat(genreRepository.count()).isEqualTo(3L);
    }
}