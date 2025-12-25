package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.dto.BookDto;

@Component
@RequiredArgsConstructor
public class BookConverter {

    public Mono<BookDto> toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setAuthorName(book.getAuthor().getFullName());
        dto.setGenreId(book.getGenre().getId());
        dto.setGenreName(book.getGenre().getName());
        return Mono.just(dto);
    }
}