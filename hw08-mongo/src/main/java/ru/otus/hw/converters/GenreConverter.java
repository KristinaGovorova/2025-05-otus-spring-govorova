package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        if (genre == null) {
            return "Genre[null]";
        }
        return "Genre(id=%s, name=%s)".formatted(
                genre.getId(),
                genre.getName());
    }
}