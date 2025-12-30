package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.BookDto;

@Component
@RequiredArgsConstructor
public class BookConverter {

    public BookDto toDto(Book book, Author author, Genre genre) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());

        dto.setAuthorId(book.getAuthorId());
        dto.setAuthorName(author.getFullName());

        dto.setGenreId(book.getGenreId());
        dto.setGenreName(genre.getName());

        return dto;
    }
}
