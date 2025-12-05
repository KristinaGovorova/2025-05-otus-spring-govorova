package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;
    private final GenreConverter genreConverter;
    private final CommentConverter commentConverter;

    public String bookToString(Book book) {
        var commentsString = book.getComments() == null ? "[]" :
                book.getComments().stream()
                        .map(commentConverter::commentToString)
                        .collect(Collectors.joining(",\n"));

        return ("Book(id=%s, title=%s, author=%s, genre=%s, comments=[\n%s\n])")
                .formatted(
                        book.getId(),
                        book.getTitle(),
                        authorConverter.authorToString(book.getAuthor()),
                        genreConverter.genreToString(book.getGenre()),
                        commentsString);
    }
}