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

INSERT INTO users (username, password, enabled) VALUES
                                                    ('user',  '{bcrypt}$2a$12$1EybM9r3ozxwSGv7uVhb1Oe7U5jd3W0lPQIJAQb.U4L6UTJQ1E1XC', true),
                                                    ('admin', '{bcrypt}$2a$12$1EybM9r3ozxwSGv7uVhb1Oe7U5jd3W0lPQIJAQb.U4L6UTJQ1E1XC', true);


INSERT INTO authorities (user_id, authority) VALUES
                                                 (1, 'ROLE_USER'),
                                                 (2, 'ROLE_USER'),
                                                 (2, 'ROLE_ADMIN');