package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookWithDetails {
    private String id;
    private String title;
    private AuthorInfo author;
    private GenreInfo genre;

    @Setter
    @Getter
    static class AuthorInfo {
        private String id;
        private String fullName;

    }

    @Setter
    @Getter
    static class GenreInfo {
        private String id;
        private String name;
    }
}
