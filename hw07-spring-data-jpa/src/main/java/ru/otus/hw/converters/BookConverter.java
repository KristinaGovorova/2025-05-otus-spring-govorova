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
        String commentsString = book.getComments() != null ?
                book.getComments().stream()
                        .map(commentConverter::commentToString)
                        .collect(Collectors.joining("; ")) :
                "No comments";

        return "Id: %d, title: %s, author: {%s}, genre: {%s}, comments: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genreConverter.genreToString(book.getGenre()),
                commentsString);
    }
}
