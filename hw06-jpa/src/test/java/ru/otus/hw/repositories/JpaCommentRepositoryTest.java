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
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class})
class JpaCommentRepositoryTest {

    @Autowired
    private JpaCommentRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;
    private List<Book> dbBooks;
    private List<Comment> dbComments;

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

        dbComments = List.of(
                new Comment("Comment_1_for_Book_1", dbBooks.get(0)),
                new Comment("Comment_2_for_Book_1", dbBooks.get(0)),
                new Comment("Comment_1_for_Book_2", dbBooks.get(1)),
                new Comment("Comment_1_for_Book_3", dbBooks.get(2))
        );

        dbComments.forEach(em::persist);
        em.flush();
        em.clear();
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        for (Comment expectedComment : dbComments) {
            var actualComment = repositoryJpa.findById(expectedComment.getId());
            assertThat(actualComment).isPresent()
                    .get()
                    .extracting(Comment::getId, Comment::getText)
                    .containsExactly(expectedComment.getId(), expectedComment.getText());

            var foundComment = actualComment.get();
            assertThat(foundComment.getBook().getId()).isEqualTo(expectedComment.getBook().getId());
            assertThat(foundComment.getBook().getTitle()).isEqualTo(expectedComment.getBook().getTitle());
        }
    }

    @DisplayName("должен загружать список всех комментариев по bookId")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var book1Comments = repositoryJpa.findByBookId(dbBooks.get(0).getId());
        assertThat(book1Comments).hasSize(2);
        assertThat(book1Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_1", "Comment_2_for_Book_1");

        var book2Comments = repositoryJpa.findByBookId(dbBooks.get(1).getId());
        assertThat(book2Comments).hasSize(1);
        assertThat(book2Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_2");

        var book3Comments = repositoryJpa.findByBookId(dbBooks.get(2).getId());
        assertThat(book3Comments).hasSize(1);
        assertThat(book3Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_3");
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = em.find(Book.class, dbBooks.get(0).getId());
        var expectedComment = new Comment("New Comment Text", book);

        var returnedComment = repositoryJpa.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("New Comment Text", book);

        assertThat(repositoryJpa.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("New Comment Text", book);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var commentToUpdate = em.find(Comment.class, dbComments.get(0).getId());
        var newBook = em.find(Book.class, dbBooks.get(1).getId());

        commentToUpdate.setText("Updated Comment Text");
        commentToUpdate.setBook(newBook);

        var returnedComment = repositoryJpa.save(commentToUpdate);

        assertThat(returnedComment)
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("Updated Comment Text", newBook);

        var foundComment = repositoryJpa.findById(returnedComment.getId());
        assertThat(foundComment)
                .isPresent()
                .get()
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("Updated Comment Text", newBook);
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var commentId = dbComments.get(0).getId();
        assertThat(repositoryJpa.findById(commentId)).isPresent();

        repositoryJpa.deleteById(commentId);

        assertThat(repositoryJpa.findById(commentId)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenCommentNotFound() {
        assertThat(repositoryJpa.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен бросать исключение при удалении несуществующего комментария")
    void delete_shouldThrowExceptionWhenCommentNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(999L));
    }

    @Test
    @DisplayName("должен бросать исключение при сохранении комментария с несуществующей книгой")
    void save_shouldThrowExceptionWhenBookNotFound() {
        var nonExistingBook = new Book(999L, "Non-existent Book", null, null);

        var newComment = new Comment("New Comment", nonExistingBook);

        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.save(newComment));
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для несуществующей книги")
    void findByBookId_shouldReturnEmptyListWhenBookNotFound() {
        var comments = repositoryJpa.findByBookId(999L);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для книги без комментариев")
    void findByBookId_shouldReturnEmptyListWhenNoCommentsForBook() {
        var newAuthor = new Author(null, "New Author");
        var newGenre = new Genre(null, "New Genre");
        em.persist(newAuthor);
        em.persist(newGenre);
        em.flush();

        var newBook = new Book("New Book Without Comments", newAuthor, newGenre);
        em.persist(newBook);
        em.flush();

        var comments = repositoryJpa.findByBookId(newBook.getId());
        assertThat(comments).isEmpty();
    }
}