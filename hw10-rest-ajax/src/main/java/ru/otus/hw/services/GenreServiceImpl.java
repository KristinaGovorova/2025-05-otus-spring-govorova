package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreConverter genreConverter;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(genreConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDto findById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        return genreConverter.toDto(genre);
    }

    @Override
    @Transactional
    public GenreDto insert(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        Genre savedGenre = genreRepository.save(genre);
        return genreConverter.toDto(savedGenre);
    }

    @Override
    @Transactional
    public GenreDto update(GenreDto genreDto) {
        Genre genre = genreRepository.findById(genreDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreDto.getId())));

        genre.setName(genreDto.getName());
        Genre updatedGenre = genreRepository.save(genre);
        return genreConverter.toDto(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (!genreRepository.existsById(id)) {
            throw new EntityNotFoundException("Genre with id %d not found".formatted(id));
        }
        genreRepository.deleteById(id);
    }
}