package com.mikolajczyk.book.backend.manager.log.service;

import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.domain.User;
import com.mikolajczyk.book.backend.manager.log.domain.Log;
import com.mikolajczyk.book.backend.manager.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository repository;

    public void save(Log log) {
        repository.save(log);
    }

    public List<Log> getLogsByUser(User user){
        return repository.findByUser(user);
    }

    public List<Log> getLogsByBook(Book book){
        return repository.findByBook(book);
    }

    public void delete(Log log) {
        repository.delete(log);
    }
}
