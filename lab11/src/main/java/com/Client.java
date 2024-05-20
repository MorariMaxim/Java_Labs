package com;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.DataBase.example.model.Author;
import com.DataBase.example.model.Book;
import com.fromLabSlides.Product;

public class Client {
    private static final String BASE_URL = "http://localhost:8081"; // Replace with your actual base URL
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();
            String[] parts = command.split(" ");

            try {
                switch (parts[0]) {
                    case "get":
                        if (parts[1].equals("authors")) {
                            getAuthors();
                        } else if (parts[1].equals("books")) {
                            getBooks();
                        } else if (parts[1].equals("book") && parts.length == 3) {
                            getBook(parts[2]);
                        }
                        break;
                    case "post":
                        if (parts[1].equals("book") && parts.length == 4) {
                            postBook(parts[2], parts[3]);
                        }
                        break;
                    case "delete":
                        if (parts[1].equals("book") && parts.length == 3) {
                            deleteBook(parts[2]);
                        }
                        break;
                    case "put":
                        if (parts[1].equals("book") && parts.length == 4) {
                            putBook(parts[2], parts[3]);
                        }
                        break;
                    default:
                        System.out.println("Unknown command");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getAuthors() throws IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<List<Author>> response = restTemplate.exchange(
                    BASE_URL + "/authors", HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Author>>() {
                    });
            List<Author> result = response.getBody();

            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Couldn't get authors: " + e.getMessage());
        }
    }

    private static void getBooks() throws IOException, InterruptedException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<Book>> response = restTemplate.exchange(
                    BASE_URL + "/books", HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Book>>() {
                    });
            List<Book> result = response.getBody();

            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Couldn't get books: " + e.getMessage());
        }

    }

    private static void getBook(String id) throws IOException, InterruptedException {

        System.out.println(BASE_URL + "/books/" + id);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Book> response = restTemplate.exchange(
                    BASE_URL + "/books/" + id, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Book>() {
                    });

            Book result = response.getBody();

            System.out.println(result);
        }

        catch (Exception e) {
            System.err.println("Couldn't get book: " + e.getMessage());
        }

    }

    private static void postBook(String title, String authorName) throws IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/books")
                .queryParam("authorName", "{authorName}")
                .queryParam("title", "{title}")
                .encode()
                .toUriString();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("authorName", authorName);
        uriVariables.put("title", title);

        String url = UriComponentsBuilder.fromUriString(urlTemplate)
                .buildAndExpand(uriVariables)
                .toUriString();

        System.out.println(url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, null,
                    new ParameterizedTypeReference<String>() {
                    });
            String result = response.getBody();

            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Couldn't post book: " + e.getMessage());
        }
    }

    private static void deleteBook(String id) throws IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/books/" + id, HttpMethod.DELETE, null,
                    new ParameterizedTypeReference<String>() {
                    });
            String result = response.getBody();

            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Couldn't delete book: " + e.getMessage());
        }
    }

    private static void putBook(String id, String newTitle) {
        RestTemplate restTemplate = new RestTemplate();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/books/" + id)
                .queryParam("newTitle", "{newTitle}")
                .encode()
                .toUriString();

        String url = UriComponentsBuilder.fromUriString(urlTemplate)
                .buildAndExpand(newTitle)
                .toUriString();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PUT, null,
                    new ParameterizedTypeReference<String>() {
                    });
            String result = response.getBody();

            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Couldn't put book: " + e.getMessage());
        }
    }
}
