package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
public class GenrePageController {

    @GetMapping
    public String listPage() {
        return "genres/list";
    }

    @GetMapping("/create")
    public String createPage() {
        return "genres/form";
    }

    @GetMapping("/{id}")
    public String viewPage(@PathVariable String id) {
        return "genres/view";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable String id) {
        return "genres/form";
    }
}