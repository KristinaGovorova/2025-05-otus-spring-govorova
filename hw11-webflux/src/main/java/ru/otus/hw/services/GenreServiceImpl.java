package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreConverter genreConverter;
    private final BookRepository bookRepository;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
                .map(genreConverter::toDto);
    }

    @Override
    public Mono<GenreDto> findById(String id) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", id)))
                .map(genreConverter::toDto);
    }

    @Override
    public Mono<GenreDto> create(GenreDto genreDto) {
        Genre genre = genreConverter.toEntity(genreDto);
        genre.setId(null);

        return genreRepository.save(genre)
                .map(genreConverter::toDto);
    }

    @Override
    public Mono<GenreDto> update(String id, GenreDto genreDto) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", id)))
                .flatMap(existingGenre -> {
                    existingGenre.setName(genreDto.getName());
                    return genreRepository.save(existingGenre);
                })
                .map(genreConverter::toDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre", id)))
                .flatMap(genre ->
                        bookRepository.deleteByGenreId(id)
                                .then(genreRepository.deleteById(id))
                );
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return genreRepository.existsById(id);
    }
}