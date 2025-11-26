package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    private final MutableAclService mutableAclService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'READ') or hasRole('ADMIN')")
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByGenreId(long genreId) {
        return bookRepository.findByGenreId(genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book insert(String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        var book = new Book(title, author, genre);
        var savedBook = bookRepository.save(book);

        var oid = new ObjectIdentityImpl(Book.class, savedBook.getId());

        MutableAcl acl = mutableAclService.createAcl(oid);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var sid = new PrincipalSid(authentication);

        acl.setOwner(sid);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sid, true);

        mutableAclService.updateAcl(acl);

        return savedBook;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'WRITE') or hasRole('ADMIN')")
    public Book update(long id, String title, long authorId, long genreId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book save(Book book) {
        if (book.getId() == null || book.getId() == 0) {
            return insert(book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        } else {
            return update(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'ADMINISTRATION') or hasRole('ADMIN')")
    public void deleteById(long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
        bookRepository.deleteById(id);
    }
}