package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.AbstractIntegrationTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с книгами")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BookRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private static final long EXISTING_BOOK_ID = 1L;
    private static final String EXISTING_BOOK_TITLE = "A Cool Book";
    private static final long EXISTING_AUTHOR_ID = 1L;
    private static final long EXISTING_GENRE_ID = 1L;

    @DisplayName("должен загружать книгу по id с комментариями")
    @Test
    void shouldReturnCorrectBookByIdWithComments() {
        var actualBook = bookRepository.findById(EXISTING_BOOK_ID);

        assertThat(actualBook).isPresent()
                .get()
                .extracting(Book::getId, Book::getTitle)
                .containsExactly(EXISTING_BOOK_ID, EXISTING_BOOK_TITLE);

        var foundBook = actualBook.get();
        assertThat(foundBook.getAuthor().getId()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(foundBook.getGenre().getId()).isEqualTo(EXISTING_GENRE_ID);
        assertThat(foundBook.getComments()).isNotNull();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();

        assertThat(actualBooks).hasSize(3);

        assertThat(actualBooks)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("A Cool Book", "A Nice Book", "A Boring Book");
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = em.find(Author.class, EXISTING_AUTHOR_ID);
        var genre = em.find(Genre.class, EXISTING_GENRE_ID);

        var expectedBook = new Book("New Test Book", author, genre);

        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook.getId()).isGreaterThan(0);

        var actualBook = em.find(Book.class, returnedBook.getId());
        assertThat(actualBook.getTitle()).isEqualTo("New Test Book");
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookToUpdate = em.find(Book.class, EXISTING_BOOK_ID);
        var newAuthor = em.find(Author.class, 2L);

        bookToUpdate.setTitle("Updated Title");
        bookToUpdate.setAuthor(newAuthor);

        bookRepository.save(bookToUpdate);
        em.flush();
        em.clear();

        var updatedBook = em.find(Book.class, EXISTING_BOOK_ID);
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2L);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookRepository.findById(EXISTING_BOOK_ID)).isPresent();

        bookRepository.deleteById(EXISTING_BOOK_ID);
        em.flush();
        em.clear();

        assertThat(bookRepository.findById(EXISTING_BOOK_ID)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenBookNotFound() {
        assertThat(bookRepository.findById(9999L)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать количество книг")
    void shouldReturnBooksCount() {
        assertThat(bookRepository.count()).isEqualTo(3L);
    }
}