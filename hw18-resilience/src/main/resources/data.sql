INSERT INTO authors (full_name) VALUES
                                    ('John Johnson'),
                                    ('Jack Jackson'),
                                    ('David Davidson');

INSERT INTO genres (name) VALUES
                              ('Fantasy'),
                              ('Sci-Fi'),
                              ('Non-Fiction');

INSERT INTO books (title, author_id, genre_id) VALUES
                                                   ('A Cool Book', 1, 1),
                                                   ('A Nice Book', 2, 2),
                                                   ('A Boring Book', 3, 3);

INSERT INTO comments (text, book_id) VALUES
                                         ('Comment_1', 1),
                                         ('Comment_2', 2),
                                         ('Comment_3', 3);