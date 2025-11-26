TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE comments RESTART IDENTITY CASCADE;
TRUNCATE TABLE books RESTART IDENTITY CASCADE;
TRUNCATE TABLE authors RESTART IDENTITY CASCADE;
TRUNCATE TABLE genres RESTART IDENTITY CASCADE;

TRUNCATE TABLE acl_entry RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_object_identity RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_sid RESTART IDENTITY CASCADE;
TRUNCATE TABLE acl_class RESTART IDENTITY CASCADE;

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

INSERT INTO users(username, password, role) VALUES
                                                ('admin', '$2a$12$5ZNxA92oQZILQFQiC6EDueMfosnkdVJTU70AKHZn/HImBfHM0eb/e', 'ROLE_ADMIN');

INSERT INTO users(username, password, role) VALUES
                                                ('user', '$2a$12$5ZNxA92oQZILQFQiC6EDueMfosnkdVJTU70AKHZn/HImBfHM0eb/e', 'ROLE_USER');