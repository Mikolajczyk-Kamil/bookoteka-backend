package com.mikolajczyk.redude.backend.sources.googleBooks.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.sources.googleBooks.engine.ExtendedSearchEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtendedSearchController {

    private final ExtendedSearchEngine engine;

    public List<Book> getBooksByQ(String q) throws UnirestException {
        log.info("Extended search by query: " + q + "...");
        return engine.doRequest(q);
    }

    public List<Book> getBooksByTitle(String title) throws UnirestException {
        log.info("Extended search by title: " + title + "...");
        String value = "intitle:" + title;
        return engine.doRequest(value);
    }

    public List<Book> getBooksByAuthor(String author) throws UnirestException {
        log.info("Extended search by author: " + author + "...");
        String value = "inauthor:" + author;
        return engine.doRequest(value);
    }

    public List<Book> getBooksByTitleAndAuthor(String title, String author) throws UnirestException {
        log.info("Extended search by title: " + title + " and author: " + author + "...");
        String value = "intitle:" + title + "+inauthor:" + author;
        return engine.doRequest(value);
    }

    public Optional<Book> getBooksByIsbn(String isbn) throws UnirestException {
        log.info("Extended search by ISBN: " + isbn + "...");
        String value = "isbn:" + isbn;
        List<Book> result = engine.doRequest(value);
        if (result.size() > 0)
            return Optional.of(result.get(0));
        return Optional.empty();
    }
}
