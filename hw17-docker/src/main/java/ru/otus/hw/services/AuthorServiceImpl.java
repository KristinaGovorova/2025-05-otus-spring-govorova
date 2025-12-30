package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorConverter authorConverter;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDto findById(long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        return authorConverter.toDto(author);
    }

    @Override
    @Transactional
    public AuthorDto insert(AuthorDto authorDto) {
        Author author = new Author();
        author.setFullName(authorDto.getFullName());
        Author savedAuthor = authorRepository.save(author);
        return authorConverter.toDto(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorDto update(AuthorDto authorDto) {
        Author author = authorRepository.findById(authorDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorDto.getId())));

        author.setFullName(authorDto.getFullName());
        Author updatedAuthor = authorRepository.save(author);
        return authorConverter.toDto(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author with id %d not found".formatted(id));
        }
        authorRepository.deleteById(id);
    }
}