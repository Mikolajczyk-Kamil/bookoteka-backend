package com.mikolajczyk.redude.backend.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository repository;

    public Book saveOrUpdate(Book book) {
        Optional<Book> result = getByGoogleId(book.getGoogleId());
        result.ifPresent(b -> {
            log.info("Book exists in database. Updating...");
            book.setId(result.get().getId());
        });
        log.info("Saving book(GOOGLE_ID: " + book.getGoogleId() + ")...");
        if (book.getDescription() != null && book.getDescription().length() > 250)
            book.setDescription(book.getDescription().substring(0, 254));
        if (book.getCategories() != null)
            book.setCategories(book.getCategories().toLowerCase());
        return repository.save(book);
    }

    public Optional<Book> getByIsbn(String isbn) {
        log.info("Getting book(ISBN: " + isbn + ")...");
        return repository.findByIsbn(isbn);
    }

    public Optional<Book> getByGoogleId(String googleId) {
        log.info("Getting book(GOOGLE_ID: " + googleId + ")");
        return repository.findByGoogleId(googleId);
    }

    public List<Book> getAllByTitle(String title) {
        log.info("Getting books(TITLE: " + title + ")...");
        return repository.findAllByTitle(title);
    }

    public List<Book> getAllByAuthor(String author) {
        log.info("Getting books(AUTHOR: " + author + ")...");
        return repository.findAllByAuthor(author);
    }

    public List<Book> getAllByCategory(String category) {
        log.info("Getting books(CATEGORY: " + category + ")...");
        return repository.findAllByCategory(category);
    }

    public List<Book> getAllByQ(String q) {
        log.info("Getting books(QUERY: " + q + ")...");
        return repository.findAllByQ(q);
    }

    public List<Book> getAllByTitleAndAuthorAndCategory(String title, String author, String category) {
        log.info("Getting books(TITLE: " + title + ", AUTHOR: " + author + ", CATEGORY: " + category + ")...");
        return repository.findAllByTitleAndAuthorAndCategory(title, author, category);
    }

    public List<Book> getAllByTitleAndAuthor(String title, String author) {
        log.info("Getting books(TITLE: " + title + ", AUTHOR: " + author + ")...");
        return repository.findAllByTitleAndAuthor(title, author);
    }

    public List<Book> getAllByTitleAndCategory(String title, String category) {
        log.info("Getting books(TITLE: " + title + ", CATEGORY: " + category + ")...");
        return repository.findAllByTitleAndCategory(title, category);
    }

    public List<Book> getAllByAuthorAndCategory(String author, String category) {
        log.info("Getting books(AUTHOR: " + author + ", CATEGORY: " + category + ")...");
        return repository.findAllByAuthorAndCategory(author, category);
    }

    public void delete(Book book) {
        log.warn("Deleting book(ISBN: " + book.getIsbn() + ", ID" + book.getId() + ") from database...");
        repository.delete(book);
    }
}

