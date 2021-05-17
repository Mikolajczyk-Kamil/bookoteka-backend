package com.mikolajczyk.redude.backend.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public Optional<User> getById(long id) {
        return repository.findById(id);
    }

    public Optional<User> getByGoogleId(String googleId) {
        return repository.findByGoogleId(googleId);
    }

    public List<User> getObservers() {
        return repository.getObservers();
    }

    public User addToRead(Book book, User user) {
        if (!user.getToRead().contains(book)) {
            user.addBookToRead(book);
            log.info("SUCCESS while adding book to 'toRead'");
            return repository.save(user);
        }
        return new User();
    }

    public void removeToRead(Book book, User user) {
        user.removeBookToRead(book);
        repository.save(user);
        log.info("SUCCESS while removing book from 'toRead'");
    }

    public User addReading(Book book, User user) {
        if (!user.getReading().contains(book)) {
            user.addBookReading(book);
            log.info("SUCCESS while adding book to 'during'");
            return repository.save(user);
        }
        return new User();
    }

    public void removeReading(Book book, User user) {
        user.removeBookReading(book);
        repository.save(user);
        log.info("SUCCESS while removing book from 'during'");
    }

    public User addHaveRead(Book book, User user) {
        if (!user.getHaveRead().contains(book)) {
            user.addBookHaveRead(book);
            log.info("SUCCESS while adding book to 'haveRead'");
            return repository.save(user);
        }
        return new User();
    }

    public void removeHaveRead(Book book, User user) {
        user.removeBookHaveRead(book);
        repository.save(user);
        log.info("SUCCESS while removing book from 'haveRead'");
    }

    public void delete(String googleId) {
        log.warn("Deleting user(GOOGLE_ID: " + googleId);
        repository.deleteByGoogleId(googleId);
    }
}
