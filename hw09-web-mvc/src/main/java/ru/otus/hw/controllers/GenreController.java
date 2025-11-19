package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;


@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping("/{id}")
    public String viewGenre(@PathVariable long id, Model model) {
        try {
            var genre = genreService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));

            var genreBooks = bookService.findByGenreId(id);

            model.addAttribute("genre", genre);
            model.addAttribute("books", genreBooks);
            model.addAttribute("title", "Жанр: " + genre.getName());
            return "genres/view";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/genres?error";
        }
    }

    @GetMapping
    public String listGenres(Model model) {
        var genres = genreService.findAll();
        model.addAttribute("genres", genres);
        model.addAttribute("title", "Список жанров");
        return "genres/list";
    }

    @GetMapping("/create")
    public String createGenreForm(Model model) {
        model.addAttribute("genre", new Genre());
        model.addAttribute("title", "Добавление жанра");
        return "genres/form";
    }

    @PostMapping("/create")
    public String createGenre(@RequestParam String name) {
        try {
            Genre genre = new Genre();
            genre.setName(name);
            genreService.save(genre);
            return "redirect:/genres";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/genres/create?error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editGenreForm(@PathVariable long id, Model model) {
        var genre = genreService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));
        model.addAttribute("genre", genre);
        model.addAttribute("title", "Редактирование жанра");
        return "genres/form";
    }

    @PostMapping("/edit/{id}")
    public String editGenre(@PathVariable long id, @RequestParam String name) {
        try {
            var genre = genreService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));
            genre.setName(name);
            genreService.save(genre);
            return "redirect:/genres";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/genres/edit/" + id + "?error";
        }
    }

    @GetMapping("/delete-confirm/{id}")
    public String deleteGenreConfirmation(@PathVariable long id, Model model) {
        try {
            var genre = genreService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));

            var genreBooks = bookService.findByGenreId(id);

            if (!genreBooks.isEmpty()) {
                model.addAttribute("genre", genre);
                model.addAttribute("books", genreBooks);
                model.addAttribute("title", "Подтверждение удаления жанра");
                return "genres/delete-confirm";
            } else {
                genreService.deleteById(id);
                return "redirect:/genres";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/genres?error";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteGenreWithBooks(@PathVariable long id,
                                       @RequestParam(defaultValue = "false") boolean deleteBooks) {
        try {
            var genre = genreService.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id));

            if (deleteBooks) {
                var genreBooks = bookService.findByGenreId(id);
                for (Book book : genreBooks) {
                    bookService.deleteById(book.getId());
                }
            }

            genreService.deleteById(id);
            return "redirect:/genres";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/genres?error";
        }
    }
}