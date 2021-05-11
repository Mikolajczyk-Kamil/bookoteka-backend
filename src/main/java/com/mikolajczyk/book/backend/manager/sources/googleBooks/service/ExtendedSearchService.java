package com.mikolajczyk.book.backend.manager.sources.googleBooks.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.service.BookService;
import com.mikolajczyk.book.backend.manager.sources.googleBooks.controller.ExtendedSearchController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExtendedSearchService {

    private final BookService bookService;
    private final ExtendedSearchController extendedSearchController;

    public List<Book> getBooksByQuery(String q, boolean externalSearch) throws UnirestException {
        List<Book> result = new ArrayList<>();
        if (!externalSearch)
            result = bookService.getAllByQ(q);
        if (result.size() < 4 || externalSearch)
            return extendedSearchController.getBooksByQ(q);
        return result;
    }

    public List<Book> getBooksByTitleAndAuthor(String title, String author, boolean externalSearch) throws UnirestException {
        List<Book> result = new ArrayList<>();
        if (!externalSearch)
            result = bookService.getAllByTitleAndAuthor(title, author);
        if (result.size() < 4 || externalSearch)
            return extendedSearchController.getBooksByTitleAndAuthor(title, author);
        return result;
    }

    public List<Book> getBooksByTitle(String title, boolean externalSearch) throws UnirestException {
        List<Book> result = new ArrayList<>();
        if (!externalSearch)
            result = bookService.getAllByTitle(title);
        if (result.size() < 4 || externalSearch)
            return extendedSearchController.getBooksByTitle(title);
        return result;
    }

    public List<Book> getBooksByAuthor(String author, boolean externalSearch) throws UnirestException {
        List<Book> result = new ArrayList<>();
        if (!externalSearch)
            result = bookService.getAllByAuthor(author);
        if (result.size() < 4 || externalSearch)
            return extendedSearchController.getBooksByAuthor(author);
        return result;
    }

    public Optional<Book> getBookByIsbn(String isbn) throws UnirestException {
        Optional<Book> result = bookService.getByIsbn(isbn);
        if (result.isEmpty())
            return extendedSearchController.getBooksByIsbn(isbn);
        return result;
    }
}
