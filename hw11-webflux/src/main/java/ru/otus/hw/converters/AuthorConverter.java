package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;

@Component
public class AuthorConverter {

    public AuthorDto toDto(Author author) {
        if (author == null) {
            return null;
        }
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setFullName(author.getFullName());
        return dto;
    }

    public Author toEntity(AuthorDto dto) {
        if (dto == null) {
            return null;
        }
        Author author = new Author();
        author.setId(dto.getId());
        author.setFullName(dto.getFullName());
        return author;
    }
}