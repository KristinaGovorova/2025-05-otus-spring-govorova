package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;


@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable long id, Model model) {
        try {
            var author = authorService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

            var authorBooks = bookService.findByAuthorId(id);

            model.addAttribute("author", author);
            model.addAttribute("books", authorBooks);
            model.addAttribute("title", "Автор: " + author.getFullName());
            return "authors/view";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/authors?error";
        }
    }


    @GetMapping
    public String listAuthors(Model model) {
        var authors = authorService.findAll();
        model.addAttribute("authors", authors);
        model.addAttribute("title", "Список авторов");
        return "authors/list";
    }

    @GetMapping("/create")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("title", "Добавление автора");
        return "authors/form";
    }

    @PostMapping("/create")
    public String createAuthor(@RequestParam String fullName) {
        try {
            Author author = new Author();
            author.setFullName(fullName);
            authorService.save(author);
            return "redirect:/authors";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/authors/create?error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editAuthorForm(@PathVariable long id, Model model) {
        var author = authorService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
        model.addAttribute("author", author);
        model.addAttribute("title", "Редактирование автора");
        return "authors/form";
    }

    @PostMapping("/edit/{id}")
    public String editAuthor(@PathVariable long id, @RequestParam String fullName) {
        try {
            var author = authorService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
            author.setFullName(fullName);
            authorService.save(author);
            return "redirect:/authors";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/authors/edit/" + id + "?error";
        }
    }

    @GetMapping("/delete-confirm/{id}")
    public String deleteAuthorConfirmation(@PathVariable long id, Model model) {
        try {
            var author = authorService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

            var authorBooks = bookService.findByAuthorId(id);

            if (!authorBooks.isEmpty()) {
                model.addAttribute("author", author);
                model.addAttribute("books", authorBooks);
                model.addAttribute("title", "Подтверждение удаления автора");
                return "authors/delete-confirm";
            } else {
                authorService.deleteById(id);
                return "redirect:/authors";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/authors?error";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthorWithBooks(@PathVariable long id,
                                        @RequestParam(defaultValue = "false") boolean deleteBooks) {
        try {
            var author = authorService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

            if (deleteBooks) {
                var authorBooks = bookService.findByAuthorId(id);
                for (Book book : authorBooks) {
                    bookService.deleteById(book.getId());
                }
            }

            authorService.deleteById(id);
            return "redirect:/authors";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/authors?error";
        }
    }
}