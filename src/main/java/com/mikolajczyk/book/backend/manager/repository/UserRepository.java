package com.mikolajczyk.book.backend.manager.repository;

import com.mikolajczyk.book.backend.manager.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByGoogleId(String googleId);

    void deleteByGoogleId(String googleId);
}
