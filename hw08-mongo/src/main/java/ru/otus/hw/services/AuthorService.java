package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();

    @Transactional
    void deleteById(String id);
}
