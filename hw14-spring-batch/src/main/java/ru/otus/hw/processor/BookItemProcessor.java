package ru.otus.hw.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.hw.entity.nosql.AuthorDocument;
import ru.otus.hw.entity.nosql.BookDocument;
import ru.otus.hw.entity.nosql.GenreDocument;
import ru.otus.hw.entity.relational.AuthorEntity;
import ru.otus.hw.entity.relational.BookEntity;
import ru.otus.hw.entity.relational.GenreEntity;
import ru.otus.hw.repository.nosql.AuthorMongoRepository;
import ru.otus.hw.repository.nosql.GenreMongoRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookItemProcessor implements ItemProcessor<BookEntity, BookDocument> {

    @Autowired
    private AuthorMongoRepository authorMongoRepository;

    @Autowired
    private GenreMongoRepository genreMongoRepository;

    private final Map<Long, AuthorDocument> authorCache = new HashMap<>();
    private final Map<Long, GenreDocument> genreCache = new HashMap<>();

    @Override
    public BookDocument process(BookEntity bookEntity) throws Exception {
        BookDocument bookDocument = new BookDocument();

        bookDocument.setTitle(bookEntity.getTitle());
        bookDocument.setPublicationYear(bookEntity.getPublicationYear());
        bookDocument.setMigratedAt(LocalDateTime.now());

        if (bookEntity.getAuthor() != null) {
            AuthorDocument authorDocument = authorCache.computeIfAbsent(
                    bookEntity.getAuthor().getId(),
                    id -> convertAuthor(bookEntity.getAuthor())
            );
            bookDocument.setAuthor(authorDocument);
        }

        if (bookEntity.getGenre() != null) {
            GenreDocument genreDocument = genreCache.computeIfAbsent(
                    bookEntity.getGenre().getId(),
                    id -> convertGenre(bookEntity.getGenre())
            );
            bookDocument.setGenre(genreDocument);
        }

        return bookDocument;
    }

    private AuthorDocument convertAuthor(AuthorEntity authorEntity) {
        AuthorDocument authorDocument = new AuthorDocument();
        authorDocument.setName(authorEntity.getName());
        authorDocument.setOriginalId(authorEntity.getId());
        return authorMongoRepository.save(authorDocument);
    }

    private GenreDocument convertGenre(GenreEntity genreEntity) {
        GenreDocument genreDocument = new GenreDocument();
        genreDocument.setName(genreEntity.getName());
        genreDocument.setOriginalId(genreEntity.getId());
        return genreMongoRepository.save(genreDocument);
    }
}
