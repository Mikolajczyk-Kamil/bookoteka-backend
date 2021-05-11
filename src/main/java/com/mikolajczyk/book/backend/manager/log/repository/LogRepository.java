package com.mikolajczyk.book.backend.manager.log.repository;

import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.domain.User;
import com.mikolajczyk.book.backend.manager.log.domain.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LogRepository extends CrudRepository<Log, Long> {

    List<Log> findByUser(User user);

    List<Log> findByBook(Book book);
}
