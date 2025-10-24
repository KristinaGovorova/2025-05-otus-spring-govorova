package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;
    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        );

        dbGenres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3")
        );

        dbAuthors.forEach(em::persist);
        dbGenres.forEach(em::persist);
        em.flush();

        dbBooks = List.of(
                new Book("BookTitle_1", dbAuthors.get(0), dbGenres.get(0)),
                new Book("BookTitle_2", dbAuthors.get(1), dbGenres.get(1)),
                new Book("BookTitle_3", dbAuthors.get(2), dbGenres.get(2))
        );

        dbBooks.forEach(em::persist);
        em.flush();
        em.clear();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        for (Book expectedBook : dbBooks) {
            var actualBook = repositoryJpa.findById(expectedBook.getId());
            assertThat(actualBook).isPresent()
                    .get()
                    .extracting(Book::getId, Book::getTitle)
                    .containsExactly(expectedBook.getId(), expectedBook.getTitle());

            var foundBook = actualBook.get();
            assertThat(foundBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
            assertThat(foundBook.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId());
        }
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repositoryJpa.findAll();
        assertThat(actualBooks).hasSize(3);

        assertThat(actualBooks)
                .extracting(Book::getTitle)
                .containsExactly("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = em.find(Author.class, dbAuthors.get(0).getId());
        var genre = em.find(Genre.class, dbGenres.get(0).getId());
        var expectedBook = new Book("BookTitle_10500", author, genre);

        var returnedBook = repositoryJpa.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("BookTitle_10500", author, genre);

        assertThat(repositoryJpa.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("BookTitle_10500", author, genre);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookToUpdate = em.find(Book.class, dbBooks.get(0).getId());
        var newAuthor = em.find(Author.class, dbAuthors.get(1).getId());
        var newGenre = em.find(Genre.class, dbGenres.get(1).getId());

        bookToUpdate.setTitle("UpdatedTitle");
        bookToUpdate.setAuthor(newAuthor);
        bookToUpdate.setGenre(newGenre);

        var returnedBook = repositoryJpa.save(bookToUpdate);

        assertThat(returnedBook)
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("UpdatedTitle", newAuthor, newGenre);

        var foundBook = repositoryJpa.findById(returnedBook.getId());
        assertThat(foundBook)
                .isPresent()
                .get()
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("UpdatedTitle", newAuthor, newGenre);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var bookId = dbBooks.get(0).getId();
        assertThat(repositoryJpa.findById(bookId)).isPresent();

        repositoryJpa.deleteById(bookId);

        assertThat(repositoryJpa.findById(bookId)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenBookNotFound() {
        assertThat(repositoryJpa.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен бросать исключение при удалении несуществующей книги")
    void delete_shouldThrowExceptionWhenBookNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(999L));
    }

    @Test
    @DisplayName("должен бросать исключение при сохранении книги с несуществующим автором")
    void save_shouldThrowExceptionWhenAuthorNotFound() {
        var genre = em.find(Genre.class, dbGenres.get(0).getId());

        var nonExistingAuthor = new Author(999L, "Non-existent Author");

        var newBook = new Book("New Book", nonExistingAuthor, genre);

        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.save(newBook));
    }
}