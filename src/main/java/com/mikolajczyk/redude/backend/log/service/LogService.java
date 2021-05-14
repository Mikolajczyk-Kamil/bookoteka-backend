package com.mikolajczyk.redude.backend.log.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.log.repository.LogRepository;
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

    public void deleteAllByUser(User user) {
        repository.deleteAllByUser(user);
    }
}
