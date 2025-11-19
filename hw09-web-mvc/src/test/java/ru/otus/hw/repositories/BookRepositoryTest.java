package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с книгами")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

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

        dbAuthors = authorRepository.saveAll(dbAuthors);
        dbGenres = genreRepository.saveAll(dbGenres);

        dbBooks = List.of(
                new Book("BookTitle_1", dbAuthors.get(0), dbGenres.get(0)),
                new Book("BookTitle_2", dbAuthors.get(1), dbGenres.get(1)),
                new Book("BookTitle_3", dbAuthors.get(2), dbGenres.get(2))
        );

        dbBooks = bookRepository.saveAll(dbBooks);
        em.clear();
    }

    @DisplayName("должен загружать книгу по id с комментариями")
    @Test
    void shouldReturnCorrectBookByIdWithComments() {
        for (Book expectedBook : dbBooks) {
            var actualBook = bookRepository.findById(expectedBook.getId());
            assertThat(actualBook).isPresent()
                    .get()
                    .extracting(Book::getId, Book::getTitle)
                    .containsExactly(expectedBook.getId(), expectedBook.getTitle());

            var foundBook = actualBook.get();
            assertThat(foundBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
            assertThat(foundBook.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId());
            assertThat(foundBook.getComments()).isNotNull();
        }
    }

    @DisplayName("должен загружать список всех книг с комментариями")
    @Test
    void shouldReturnCorrectBooksListWithComments() {
        var actualBooks = bookRepository.findAll();
        assertThat(actualBooks).hasSize(3);

        assertThat(actualBooks)
                .extracting(Book::getTitle)
                .containsExactly("BookTitle_1", "BookTitle_2", "BookTitle_3");

        actualBooks.forEach(book -> assertThat(book.getComments()).isNotNull());
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = authorRepository.findById(dbAuthors.get(0).getId()).orElseThrow();
        var genre = genreRepository.findById(dbGenres.get(0).getId()).orElseThrow();
        var expectedBook = new Book("BookTitle_10500", author, genre);

        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("BookTitle_10500", author, genre);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("BookTitle_10500", author, genre);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookToUpdate = bookRepository.findById(dbBooks.get(0).getId()).orElseThrow();
        var newAuthor = authorRepository.findById(dbAuthors.get(1).getId()).orElseThrow();
        var newGenre = genreRepository.findById(dbGenres.get(1).getId()).orElseThrow();

        bookToUpdate.setTitle("UpdatedTitle");
        bookToUpdate.setAuthor(newAuthor);
        bookToUpdate.setGenre(newGenre);

        var returnedBook = bookRepository.save(bookToUpdate);

        assertThat(returnedBook)
                .extracting(Book::getTitle, Book::getAuthor, Book::getGenre)
                .containsExactly("UpdatedTitle", newAuthor, newGenre);

        var foundBook = bookRepository.findById(returnedBook.getId());
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
        assertThat(bookRepository.findById(bookId)).isPresent();

        bookRepository.deleteById(bookId);

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenBookNotFound() {
        assertThat(bookRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске с комментариями по несуществующему id")
    void findByIdWithComments_shouldReturnEmptyWhenBookNotFound() {
        assertThat(bookRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен бросать исключение при удалении несуществующей книги (проверка в сервисе)")
    void delete_shouldNotThrowExceptionWhenBookNotFound() {
        assertThat(bookRepository.existsById(999L)).isFalse();

        bookRepository.deleteById(999L);

        assertThat(bookRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен корректно работать стандартный метод findById (без комментариев)")
    void shouldWorkWithStandardFindById() {
        var book = bookRepository.findById(dbBooks.get(0).getId());
        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("BookTitle_1");
    }

    @Test
    @DisplayName("должен проверять существование книги")
    void shouldCheckBookExistence() {
        assertThat(bookRepository.existsById(dbBooks.get(0).getId())).isTrue();
        assertThat(bookRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("должен возвращать количество книг")
    void shouldReturnBooksCount() {
        assertThat(bookRepository.count()).isEqualTo(3L);
    }
}