package com.mikolajczyk.redude.backend.repository;

import com.mikolajczyk.redude.backend.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByGoogleId(String googleId);

    void deleteByGoogleId(String googleId);

    @Query
    List<User> getObservers();
}
