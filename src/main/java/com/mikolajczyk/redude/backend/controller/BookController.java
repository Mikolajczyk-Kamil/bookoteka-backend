package com.mikolajczyk.redude.backend.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.rating.dto.RatingDto;
import com.mikolajczyk.redude.backend.log.LogController;
import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.log.type.LogType;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.rating.mapper.RatingMapper;
import com.mikolajczyk.redude.backend.service.BookService;
import com.mikolajczyk.redude.backend.rating.service.RatingService;
import com.mikolajczyk.redude.backend.service.UserService;
import com.mikolajczyk.redude.backend.sources.googleBooks.service.ExtendedSearchService;
import com.mikolajczyk.redude.backend.verifier.TokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1/books")
@CrossOrigin("*")
public class BookController {

    private final TokenVerifier verifier;
    private final UserService userService;
    private final BookService bookService;
    private final BookMapper bookMapper;
    private final RatingMapper ratingMapper;
    private final RatingService ratingService;
    private final ExtendedSearchService extendedSearchService;
    private final LogController logController;

    @GetMapping
    public List<BookDto> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false, defaultValue = "false") boolean external,
            @RequestHeader("Authorization") String token
    ) {
        try {
            log.info("Trying to verifying user....");
            User user = verifier.verify(token);
            if (user != null) {
                log.info("SUCCESS");
                if (q != null)
                    return bookMapper.mapToListBookDto(extendedSearchService.getBooksByQuery(q, external));
                else if (title != null && author != null && category != null)
                    return bookMapper.mapToListBookDto(bookService.getAllByTitleAndAuthorAndCategory(title, author, category));
                else if (title != null && author != null)
                    return bookMapper.mapToListBookDto(extendedSearchService.getBooksByTitleAndAuthor(title, author, external));
                else if (title != null && category != null)
                    return bookMapper.mapToListBookDto(bookService.getAllByTitleAndCategory(title, category));
                else if (author != null && category != null)
                    return bookMapper.mapToListBookDto(bookService.getAllByAuthorAndCategory(author, category));
                else if (title != null)
                    return bookMapper.mapToListBookDto(extendedSearchService.getBooksByTitle(title, external));
                else if (author != null)
                    return bookMapper.mapToListBookDto(extendedSearchService.getBooksByAuthor(author, external));
                else if (category != null)
                    return bookMapper.mapToListBookDto(bookService.getAllByCategory(category));
                else if (isbn != null) {
                    Optional<Book> result = extendedSearchService.getBookByIsbn(isbn);
                    List<Book> list = new ArrayList<>();
                    result.ifPresent(list::add);
                    return bookMapper.mapToListBookDto(list);
                }
            }
        } catch (UnirestException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        log.info("FAILED");
        return List.of();
    }

    @GetMapping("/{bookId}")
    public List<RatingDto> getBookRating(@PathVariable String bookId, @RequestHeader("Authorization") String token) {
        log.info("Trying to get rating(BOOK_ID: " + bookId + ")...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Optional<Book> optionalBook = bookService.getByGoogleId(bookId);
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty()) {
                    log.info("Book not found");
                    return List.of();
                }
                List<Rating> bookRatings = ratingService.getAllRatingsByBook(optionalBook.get());
                log.info("SUCCESS");
                return ratingMapper.mapToListRatingDto(bookRatings);
            }
        }
        log.info("FAILED");
        return List.of();
    }

    @PostMapping(value = "/{googleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public long rateBook(@PathVariable String googleId, @RequestBody RatingDto ratingDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to rate book(GOOGLE_ID: " + googleId + ")...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE VERIFYING USER");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            if (optionalUser.isPresent()) {
                Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
                Rating rating = ratingMapper.mapToRating(ratingDto);
                Book book;
                if (optionalBook.isEmpty()) {
                    if (rating.getBook() != null) {
                        log.info("Adding new book to database...");
                        book = bookService.saveOrUpdate(rating.getBook());
                    } else {
                        log.info("Book not found");
                        return 0;
                    }
                } else
                    book = optionalBook.get();
                rating.setUser(optionalUser.get());
                rating.setBook(book);
                optionalUser.get().getRatings().add(rating);
                book.getRatings().add(rating);
                ratingService.save(rating);
                logController.log(new Log(LogType.ADD_RATING, optionalUser.get(), book));
                log.info("SUCCESS");
                return rating.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public long deleteRating(@RequestBody RatingDto ratingDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to delete rating...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE VERIFYING USER");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            if (optionalUser.isPresent()) {
                Optional<Book> optionalBook = bookService.getByGoogleId(ratingDto.getBookDto().getGoogleId());
                if (optionalBook.isEmpty()) {
                    log.info("Book not found");
                    return 0;
                }
                Optional<Rating> optionalRating = ratingService.getById(ratingDto.getId());
                if (optionalRating.isEmpty()) {
                    log.info("Rating not found");
                    return 0;
                }
                ratingService.delete(optionalRating.get());
                log.info("SUCCESS");
                logController.log(new Log(LogType.DELETE_RATING, optionalUser.get(), optionalBook.get()));
                return ratingDto.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }
}
