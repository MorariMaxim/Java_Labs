package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataImporter {
    private static final String CSV_FILE_PATH = "goodreads_books.csv";
    private static final String CSV_FILE_PATH1 = "Goodreads_books_with_genres.csv";

    public static void main(String[] args) {

        int count = 0;

        AuthorDAO authorDAO = new AuthorDAO();
        BookDAO bookDAO = new BookDAO();
        AuthorshipDao authorshipDao = new AuthorshipDao();
        GenresDao genresDao = new GenresDao();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH1))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null && (count < 1000)) {

                String[] parts = line.split(",", 13);

                String[] genres = trimGenres(parts[12]).split("[,;]");

                java.util.Date publicationDate = getDateFromGoodReadsDateFormat(parts[10]);

                // Trim and collect the elements into a List
                List<String> authors = Arrays.stream(parts[2].split("/"))
                        .map(String::trim) // Trim each element
                        .collect(Collectors.toList());

                String title = parts[1];
                Integer id = Integer.parseInt(parts[0]);
                String language = parts[6];
                Integer pages = Integer.parseInt(parts[7]);

                Book book = new Book(id, title, language, publicationDate, pages);

                /*
                 * System.out.println(String.format(
                 * "bookId = %s; title =  %s; authors = %s; language = %s; publicationDate = %s; pages = %s"
                 * ,
                 * id, title, "...", language,
                 * publicationDate, pages));
                 */

                try {
                    bookDAO.addBook(book);

                    for (String genre : genres) {
                        genresDao.addGenreToBook(book.getId(), genre);
                    }

                    for (String author : authors) {

                        authorDAO.addAuthor(author);

                        authorshipDao.addAuthorship(book.getId(), authorDAO.getAuthorId(author));

                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                count++;
            }
            System.out.println("Data imported successfully.");

        } catch (IOException e) {
            System.err.println("Failed to read CSV file.");
            e.printStackTrace();
        }
    }

    static java.sql.Date getDateFromGoodReadsDateFormat(String dateString) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyy");

        try {
            java.sql.Date date = new java.sql.Date(dateFormat.parse(dateString).getTime());
            return date;
        } catch (ParseException e) {
            System.err.println("Failed to parse date string: " + dateString);
            e.printStackTrace();
        }
        return null;
    }

    static String trimGenres(String input) {

        input = input.trim();
        if (input.startsWith("\"")) {
            input = input.substring(1);
        }

        if (input.endsWith("\"")) {
            input = input.substring(0, input.length() - 1);
        }

        return input;
    }
}
