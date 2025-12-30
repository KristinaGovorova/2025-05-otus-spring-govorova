package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookPageController {

    @GetMapping
    public String listPage() {
        return "books/list";
    }

    @GetMapping("/create")
    public String createPage() {
        return "books/form";
    }

    @GetMapping("/{id}")
    public String viewPage(@PathVariable String id) {
        return "books/view";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable String id) {
        return "books/form";
    }
}
