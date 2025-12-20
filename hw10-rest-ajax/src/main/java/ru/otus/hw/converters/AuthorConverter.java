package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;

@Component
public class AuthorConverter {

    public String authorToString(Author author) {
        return "Id: %d, FullName: %s".formatted(author.getId(), author.getFullName());
    }

    public AuthorDto toDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setFullName(author.getFullName());
        return dto;
    }

    public Author toEntity(AuthorDto dto) {
        Author author = new Author();
        author.setId(dto.getId());
        author.setFullName(dto.getFullName());
        return author;
    }
}