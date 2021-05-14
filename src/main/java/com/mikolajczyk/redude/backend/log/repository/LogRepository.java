package com.mikolajczyk.redude.backend.log.repository;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.log.domain.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LogRepository extends CrudRepository<Log, Long> {

    List<Log> findByUser(User user);

    List<Log> findByBook(Book book);

    void deleteAllByUser(User user);
}
