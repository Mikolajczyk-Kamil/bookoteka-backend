package com.mikolajczyk.redude.backend.rating.repository;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RatingRepository extends CrudRepository<Rating, Long> {

    List<Rating> findAllByBook(Book book);

    void deleteAllByUser(User user);
}
