package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorConverter authorConverter;
    private final BookRepository bookRepository;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(authorConverter::toDto);
    }

    @Override
    public Mono<AuthorDto> findById(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", id)))
                .map(authorConverter::toDto);
    }

    @Override
    public Mono<AuthorDto> create(AuthorDto authorDto) {
        Author author = authorConverter.toEntity(authorDto);
        author.setId(null);

        return authorRepository.save(author)
                .map(authorConverter::toDto);
    }

    @Override
    public Mono<AuthorDto> update(String id, AuthorDto authorDto) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", id)))
                .flatMap(existingAuthor -> {
                    existingAuthor.setFullName(authorDto.getFullName());
                    return authorRepository.save(existingAuthor);
                })
                .map(authorConverter::toDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author", id)))
                .flatMap(author ->
                        bookRepository.deleteByAuthorId(id)
                                .then(authorRepository.deleteById(id))
                );
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return authorRepository.existsById(id);
    }
}