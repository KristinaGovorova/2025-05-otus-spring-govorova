package ru.otus.hw.services;

import ru.otus.hw.models.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(long id);

    AuthorDto insert(AuthorDto authorDto);

    AuthorDto update(AuthorDto authorDto);

    void deleteById(long id);
}