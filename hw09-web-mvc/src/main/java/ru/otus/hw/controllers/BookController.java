package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;


@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping
    public String listBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("title", "Список книг");
        return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable long id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        model.addAttribute("book", book);
        model.addAttribute("title", "Просмотр книги");
        return "books/view";
    }

    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("title", "Добавление книги");
        return "books/form";
    }

    @PostMapping("/create")
    public String createBook(@RequestParam String title,
                             @RequestParam long authorId,
                             @RequestParam long genreId) {
        try {
            bookService.insert(title, authorId, genreId);
            return "redirect:/books";
        } catch (Exception e) {
            return "redirect:/books/create?error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable long id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("title", "Редактирование книги");
        return "books/form";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable long id,
                           @RequestParam String title,
                           @RequestParam long authorId,
                           @RequestParam long genreId) {
        try {
            bookService.update(id, title, authorId, genreId);
            return "redirect:/books";
        } catch (Exception e) {
            return "redirect:/books/edit/" + id + "?error";
        }
    }

    @GetMapping("/delete-confirm/{id}")
    public String deleteBookConfirmation(@PathVariable long id, Model model) {
        try {
            var book = bookService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
            model.addAttribute("book", book);
            model.addAttribute("title", "Подтверждение удаления книги");
            return "books/delete-confirm";
        } catch (Exception e) {
            return "redirect:/books?error";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        try {
            bookService.deleteById(id);
            return "redirect:/books";
        } catch (Exception e) {
            return "redirect:/books?error";
        }
    }
}