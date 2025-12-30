package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorPageController {

    @GetMapping
    public String listPage() {
        return "authors/list";
    }

    @GetMapping("/create")
    public String createPage() {
        return "authors/form";
    }

    @GetMapping("/{id}")
    public String viewPage(@PathVariable Long id) {
        return "authors/view";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id) {
        return "authors/form";
    }
}