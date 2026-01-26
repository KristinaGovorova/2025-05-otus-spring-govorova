package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.entity.nosql.AuthorDocument;
import ru.otus.hw.entity.nosql.GenreDocument;
import ru.otus.hw.entity.relational.AuthorEntity;
import ru.otus.hw.entity.relational.BookEntity;
import ru.otus.hw.entity.relational.GenreEntity;
import ru.otus.hw.processor.BookItemProcessor;
import ru.otus.hw.repository.nosql.AuthorMongoRepository;
import ru.otus.hw.repository.nosql.GenreMongoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookItemProcessorTest {

    @Mock
    private AuthorMongoRepository authorMongoRepository;

    @Mock
    private GenreMongoRepository genreMongoRepository;

    @InjectMocks
    private BookItemProcessor processor;

    private BookEntity bookEntity;
    private AuthorEntity authorEntity;
    private GenreEntity genreEntity;

    @BeforeEach
    void setUp() {
        authorEntity = new AuthorEntity();
        authorEntity.setId(1L);
        authorEntity.setName("Test Author");

        genreEntity = new GenreEntity();
        genreEntity.setId(1L);
        genreEntity.setName("Test Genre");

        bookEntity = new BookEntity();
        bookEntity.setId(1L);
        bookEntity.setTitle("Test Book");
        bookEntity.setAuthor(authorEntity);
        bookEntity.setGenre(genreEntity);
        bookEntity.setPublicationYear(2023);
    }

    @Test
    void testProcessBookWithAllFields() throws Exception {
        AuthorDocument mockAuthorDoc = new AuthorDocument();
        mockAuthorDoc.setId("author1");
        mockAuthorDoc.setName("Test Author");
        mockAuthorDoc.setOriginalId(1L);

        GenreDocument mockGenreDoc = new GenreDocument();
        mockGenreDoc.setId("genre1");
        mockGenreDoc.setName("Test Genre");
        mockGenreDoc.setOriginalId(1L);

        when(authorMongoRepository.save(any(AuthorDocument.class)))
                .thenReturn(mockAuthorDoc);
        when(genreMongoRepository.save(any(GenreDocument.class)))
                .thenReturn(mockGenreDoc);

        var result = processor.process(bookEntity);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
        assertThat(result.getPublicationYear()).isEqualTo(2023);
        assertThat(result.getAuthor()).isEqualTo(mockAuthorDoc);
        assertThat(result.getGenre()).isEqualTo(mockGenreDoc);
        assertThat(result.getMigratedAt()).isNotNull();
    }

    @Test
    void testProcessBookWithoutAuthorAndGenre() throws Exception {
        bookEntity.setAuthor(null);
        bookEntity.setGenre(null);

        var result = processor.process(bookEntity);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
        assertThat(result.getAuthor()).isNull();
        assertThat(result.getGenre()).isNull();
    }

    @Test
    void testProcessorCachesAuthorsAndGenres() throws Exception {
        AuthorDocument mockAuthorDoc = new AuthorDocument();
        mockAuthorDoc.setId("author1");
        mockAuthorDoc.setName("Test Author");
        mockAuthorDoc.setOriginalId(1L);

        GenreDocument mockGenreDoc = new GenreDocument();
        mockGenreDoc.setId("genre1");
        mockGenreDoc.setName("Test Genre");
        mockGenreDoc.setOriginalId(1L);

        when(authorMongoRepository.save(any(AuthorDocument.class)))
                .thenReturn(mockAuthorDoc);
        when(genreMongoRepository.save(any(GenreDocument.class)))
                .thenReturn(mockGenreDoc);

        var result1 = processor.process(bookEntity);

        var result2 = processor.process(bookEntity);

        assertThat(result1.getAuthor()).isSameAs(result2.getAuthor());
        assertThat(result1.getGenre()).isSameAs(result2.getGenre());
    }
}
