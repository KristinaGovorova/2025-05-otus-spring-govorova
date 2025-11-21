package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.AbstractIntegrationTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с комментариями")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private static final long EXISTING_COMMENT_ID = 1L;
    private static final String EXISTING_COMMENT_TEXT = "Comment_1";
    private static final long EXISTING_BOOK_ID = 1L;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var actualComment = commentRepository.findById(EXISTING_COMMENT_ID);

        assertThat(actualComment).isPresent()
                .get()
                .extracting(Comment::getId, Comment::getText)
                .containsExactly(EXISTING_COMMENT_ID, EXISTING_COMMENT_TEXT);

        var foundComment = actualComment.get();
        assertThat(foundComment.getBook().getId()).isEqualTo(EXISTING_BOOK_ID);
    }

    @DisplayName("должен загружать список всех комментариев по bookId")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var book1Comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);

        assertThat(book1Comments).hasSize(1);
        assertThat(book1Comments.get(0).getText()).isEqualTo(EXISTING_COMMENT_TEXT);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = em.find(Book.class, EXISTING_BOOK_ID);
        var expectedComment = new Comment("New Comment Text", book);

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment.getId()).isGreaterThan(0);

        var actualComment = em.find(Comment.class, returnedComment.getId());
        assertThat(actualComment.getText()).isEqualTo("New Comment Text");
        assertThat(actualComment.getBook().getId()).isEqualTo(EXISTING_BOOK_ID);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var commentToUpdate = em.find(Comment.class, EXISTING_COMMENT_ID);
        var newBook = em.find(Book.class, 2L);

        commentToUpdate.setText("Updated Comment Text");
        commentToUpdate.setBook(newBook);

        commentRepository.save(commentToUpdate);
        em.flush();
        em.clear();

        var updatedComment = em.find(Comment.class, EXISTING_COMMENT_ID);
        assertThat(updatedComment.getText()).isEqualTo("Updated Comment Text");
        assertThat(updatedComment.getBook().getId()).isEqualTo(2L);
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(commentRepository.findById(EXISTING_COMMENT_ID)).isPresent();

        commentRepository.deleteById(EXISTING_COMMENT_ID);
        em.flush();
        em.clear();

        assertThat(commentRepository.findById(EXISTING_COMMENT_ID)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenCommentNotFound() {
        assertThat(commentRepository.findById(9999L)).isEmpty();
    }

    @Test
    @DisplayName("не должен бросать исключение при удалении несуществующего комментария")
    void delete_shouldNotThrowExceptionWhenCommentNotFound() {
        assertThat(commentRepository.existsById(9999L)).isFalse();

        commentRepository.deleteById(9999L);

        assertThat(commentRepository.findById(9999L)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для несуществующей книги")
    void findByBookId_shouldReturnEmptyListWhenBookNotFound() {
        var comments = commentRepository.findAllByBookId(9999L);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для книги без комментариев")
    void findByBookId_shouldReturnEmptyListWhenNoCommentsForBook() {
        var newAuthor = new Author(null, "New Author");
        var newGenre = new Genre(null, "New Genre");
        em.persist(newAuthor);
        em.persist(newGenre);

        var newBook = new Book("New Book Without Comments", newAuthor, newGenre);
        em.persist(newBook);
        em.flush();

        var comments = commentRepository.findAllByBookId(newBook.getId());
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать количество комментариев")
    void shouldReturnCommentsCount() {
        assertThat(commentRepository.count()).isEqualTo(3L);
    }
}