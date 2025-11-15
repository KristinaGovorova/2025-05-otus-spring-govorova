package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с комментариями")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

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
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbAuthors = List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        );
        dbAuthors = authorRepository.saveAll(dbAuthors);

        dbGenres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3")
        );
        dbGenres = genreRepository.saveAll(dbGenres);

        dbBooks = List.of(
                new Book("BookTitle_1", dbAuthors.get(0), dbGenres.get(0)),
                new Book("BookTitle_2", dbAuthors.get(1), dbGenres.get(1)),
                new Book("BookTitle_3", dbAuthors.get(2), dbGenres.get(2))
        );
        dbBooks = bookRepository.saveAll(dbBooks);

        dbComments = List.of(
                new Comment("Comment_1_for_Book_1", dbBooks.get(0)),
                new Comment("Comment_2_for_Book_1", dbBooks.get(0)),
                new Comment("Comment_1_for_Book_2", dbBooks.get(1)),
                new Comment("Comment_1_for_Book_3", dbBooks.get(2))
        );
        dbComments = commentRepository.saveAll(dbComments);

        em.clear();
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        for (Comment expectedComment : dbComments) {
            var actualComment = commentRepository.findById(expectedComment.getId());
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
        var book1Comments = commentRepository.findByBookId(dbBooks.get(0).getId());
        assertThat(book1Comments).hasSize(2);
        assertThat(book1Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_1", "Comment_2_for_Book_1");

        var book2Comments = commentRepository.findByBookId(dbBooks.get(1).getId());
        assertThat(book2Comments).hasSize(1);
        assertThat(book2Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_2");

        var book3Comments = commentRepository.findByBookId(dbBooks.get(2).getId());
        assertThat(book3Comments).hasSize(1);
        assertThat(book3Comments)
                .extracting(Comment::getText)
                .containsExactly("Comment_1_for_Book_3");
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = bookRepository.findById(dbBooks.get(0).getId()).orElseThrow();
        var expectedComment = new Comment("New Comment Text", book);

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("New Comment Text", book);

        assertThat(commentRepository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("New Comment Text", book);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var commentToUpdate = commentRepository.findById(dbComments.get(0).getId()).orElseThrow();
        var newBook = bookRepository.findById(dbBooks.get(1).getId()).orElseThrow();

        commentToUpdate.setText("Updated Comment Text");
        commentToUpdate.setBook(newBook);

        var returnedComment = commentRepository.save(commentToUpdate);

        assertThat(returnedComment)
                .extracting(Comment::getText, Comment::getBook)
                .containsExactly("Updated Comment Text", newBook);

        var foundComment = commentRepository.findById(returnedComment.getId());
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
        assertThat(commentRepository.findById(commentId)).isPresent();

        commentRepository.deleteById(commentId);

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать Optional.empty при поиске по несуществующему id")
    void findById_shouldReturnEmptyWhenCommentNotFound() {
        assertThat(commentRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("не должен бросать исключение при удалении несуществующего комментария")
    void delete_shouldNotThrowExceptionWhenCommentNotFound() {
        assertThat(commentRepository.existsById(999L)).isFalse();

        commentRepository.deleteById(999L);

        assertThat(commentRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для несуществующей книги")
    void findByBookId_shouldReturnEmptyListWhenBookNotFound() {
        var comments = commentRepository.findByBookId(999L);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать пустой список при поиске комментариев для книги без комментариев")
    void findByBookId_shouldReturnEmptyListWhenNoCommentsForBook() {
        var newAuthor = new Author(null, "New Author");
        var newGenre = new Genre(null, "New Genre");
        newAuthor = authorRepository.save(newAuthor);
        newGenre = genreRepository.save(newGenre);

        var newBook = new Book("New Book Without Comments", newAuthor, newGenre);
        newBook = bookRepository.save(newBook);

        var comments = commentRepository.findByBookId(newBook.getId());
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("должен проверять существование комментария")
    void shouldCheckCommentExistence() {
        assertThat(commentRepository.existsById(dbComments.get(0).getId())).isTrue();
        assertThat(commentRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("должен возвращать количество комментариев")
    void shouldReturnCommentsCount() {
        assertThat(commentRepository.count()).isEqualTo(4L);
    }

    @Test
    @DisplayName("должен удалять все комментарии")
    void shouldDeleteAllComments() {
        assertThat(commentRepository.count()).isEqualTo(4L);

        commentRepository.deleteAll();

        assertThat(commentRepository.count()).isZero();
    }
}