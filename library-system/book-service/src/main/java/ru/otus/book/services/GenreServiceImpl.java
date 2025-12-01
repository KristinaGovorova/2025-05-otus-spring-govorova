package ru.otus.book.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.book.models.Genre;
import ru.otus.book.repositories.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre save(Genre author) {
        return genreRepository.save(author);
    }

    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
