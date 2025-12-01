TRUNCATE TABLE books RESTART IDENTITY CASCADE;
TRUNCATE TABLE authors RESTART IDENTITY CASCADE;
TRUNCATE TABLE genres RESTART IDENTITY CASCADE;

-- Авторы
INSERT INTO authors (full_name) VALUES
                                    ('Frank Herbert'),
                                    ('J.R.R. Tolkien'),
                                    ('George Orwell');

-- Жанры
INSERT INTO genres (name) VALUES
                              ('Sci-Fi'),
                              ('Fantasy'),
                              ('Dystopia');

-- Книги
INSERT INTO books (title, author_id, genre_id, total_copies, available_copies) VALUES
                                                                                   ('Dune', 1, 1, 1, 1),
                                                                                   ('The Hobbit', 2, 2, 5, 5),
                                                                                   ('1984', 3, 3, 10, 10);