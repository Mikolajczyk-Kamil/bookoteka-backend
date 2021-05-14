package com.mikolajczyk.redude.backend.rating.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository repository;

    public Rating save(Rating rating) {
        return repository.save(rating);
    }

    public Optional<Rating> getById(long id) {
        return repository.findById(id);
    }

    public void delete(Rating rating) {
        repository.delete(rating);
    }

    public void deleteAllByUser(User user) {
        repository.deleteAllByUser(user);
    }

    public List<Rating> getAllRatingsByBook(Book book) {
        return repository.findAllByBook(book);
    }
}
