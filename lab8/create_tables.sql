drop table authorship;
drop table books;
drop table authors;

CREATE TABLE books (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    language VARCHAR(50),
    publication DATE,
    pages INT
);

CREATE TABLE authors (
    id INT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE authorship (
    book_id INT,
    author_id INT,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE genres (
    book_id INT,
    genre_name VARCHAR(50),
    PRIMARY KEY (book_id, genre_name),
    FOREIGN KEY (book_id) REFERENCES books(id)
);