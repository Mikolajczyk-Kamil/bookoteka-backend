package com.mikolajczyk.redude.backend.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.controller.response.StatusResponse;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.rating.dto.RatingDto;
import com.mikolajczyk.redude.backend.log.LogController;
import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.log.type.LogType;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.controller.response.ApiResponse;
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
    public ApiResponse getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false, defaultValue = "false") boolean external,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null)
            return new ApiResponse(null, StatusResponse.TOKEN_NOT_FOUND);
        try {
            User user = verifier.verify(token);
            if (user != null) {
                if (q != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(extendedSearchService.getBooksByQuery(q, external)), StatusResponse.SUCCESS);
                else if (title != null && author != null && category != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(bookService.getAllByTitleAndAuthorAndCategory(title, author, category)), StatusResponse.SUCCESS);
                else if (title != null && author != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(extendedSearchService.getBooksByTitleAndAuthor(title, author, external)), StatusResponse.SUCCESS);
                else if (title != null && category != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(bookService.getAllByTitleAndCategory(title, category)), StatusResponse.SUCCESS);
                else if (author != null && category != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(bookService.getAllByAuthorAndCategory(author, category)), StatusResponse.SUCCESS);
                else if (title != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(extendedSearchService.getBooksByTitle(title, external)), StatusResponse.SUCCESS);
                else if (author != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(extendedSearchService.getBooksByAuthor(author, external)), StatusResponse.SUCCESS);
                else if (category != null)
                    return new ApiResponse(bookMapper.mapToListBookDto(bookService.getAllByCategory(category)), StatusResponse.SUCCESS);
                else if (isbn != null) {
                    Optional<Book> result = extendedSearchService.getBookByIsbn(isbn);
                    List<Book> list = new ArrayList<>();
                    result.ifPresent(list::add);
                    return new ApiResponse(bookMapper.mapToListBookDto(list), StatusResponse.SUCCESS);
                }
            }
        } catch (GeneralSecurityException | IOException | UnirestException e) {
            e.printStackTrace();
        }
        return new ApiResponse(null, StatusResponse.VERIFICATION_FAILED);
    }

    @PostMapping(value = "/{googleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse rateBook(@PathVariable String googleId, @RequestBody RatingDto ratingDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to rate book(GOOGLE_ID: " + googleId + ")...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
                if (optionalBook.isEmpty()) {
                    log.info("Book not found");
                    return StatusResponse.BOOK_NOT_FOUND;
                }
                if (optionalUser.isEmpty()) {
                    log.info("User not found");
                    return StatusResponse.USER_NOT_FOUND;
                }
                Rating rating = ratingMapper.mapToRating(ratingDto);
                rating.setUser(optionalUser.get());
                rating.setBook(optionalBook.get());
                optionalUser.get().getRatings().add(rating);
                optionalBook.get().getRatings().add(rating);
                ratingService.save(rating);
                log.info("SUCCESS");
                logController.log(new Log(LogType.ADD_RATING ,optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE RATING BOOK");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse updateRating(@RequestBody RatingDto ratingDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to update rating...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByGoogleId(ratingDto.getBookDto().getGoogleId());
                if (optionalBook.isEmpty()) {
                    log.info("Book not found");
                    return StatusResponse.BOOK_NOT_FOUND;
                }
                if (optionalUser.isEmpty()) {
                    log.info("User not found");
                    return StatusResponse.USER_NOT_FOUND;
                }
                Rating rating = ratingMapper.mapToRating(ratingDto);
                ratingService.save(rating);
                log.info("SUCCESS");
                logController.log(new Log(LogType.UPDATE_RATING ,optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE UPDATE RATING");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse deleteRating(@RequestBody RatingDto ratingDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to delete rating...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByGoogleId(ratingDto.getBookDto().getGoogleId());
                if (optionalBook.isEmpty()) {
                    log.info("Book not found");
                    return StatusResponse.BOOK_NOT_FOUND;
                }
                if (optionalUser.isEmpty()) {
                    log.info("User not found");
                    return StatusResponse.USER_NOT_FOUND;
                }
                Optional<Rating> optionalRating = ratingService.getById(ratingDto.getId());
                if (optionalRating.isEmpty()) {
                    log.info("RATING NOT FOUND");
                    return StatusResponse.FAILED;
                }
                ratingService.delete(optionalRating.get());
                log.info("SUCCESS");
                logController.log(new Log(LogType.DELETE_RATING ,optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE UPDATE RATING");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }
}
