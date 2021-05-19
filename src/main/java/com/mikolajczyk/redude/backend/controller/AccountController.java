package com.mikolajczyk.redude.backend.controller;

import com.mikolajczyk.redude.backend.controller.response.StatusResponse;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.log.LogController;
import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.log.type.LogType;
import com.mikolajczyk.redude.backend.mail.controller.MailController;
import com.mikolajczyk.redude.backend.mail.type.MailType;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.mapper.UserMapper;
import com.mikolajczyk.redude.backend.service.BookService;
import com.mikolajczyk.redude.backend.rating.service.RatingService;
import com.mikolajczyk.redude.backend.service.UserService;
import com.mikolajczyk.redude.backend.verifier.TokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1/account")
@CrossOrigin("*")
public class AccountController {

    private final BookService bookService;
    private final UserService userService;
    private final BookMapper bookMapper;
    private final TokenVerifier verifier;
    private final RatingService ratingService;
    private final LogController logController;
    private final MailController mailController;

    @PostMapping
    public long signIn(@RequestHeader("Authorization") String token) {
        log.info("Signing in...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE SIGNING IN USER");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            if (optionalUser.isEmpty()) {
                log.info("User not found. Sign up new user...");
                user.setObserver(true);
                user = userService.save(user);
                mailController.createAndSend(user.getName(), user.getEmail(), "Welcome to Redudo!", MailType.WELCOME);
                logController.log(new Log(LogType.SIGN_UP, user, null));
            } else
                user = optionalUser.get();
            logController.log(new Log(LogType.SIGN_IN, user, null));
            log.info("SUCCESS");
            return user.getId();
        }
        log.info("FAILED");
        return 0;
    }

    @DeleteMapping
    public long delete(@RequestHeader("Authorization") String token) {
        log.info("Trying to delete user...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE DELETING USER");
            e.printStackTrace();
        }
        if (user != null) {
            log.info("(GOOGLE_ID: " + user.getGoogleId() + ")");
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            if (optionalUser.isPresent()) {
                logController.deleteAllUserLog(optionalUser.get());
                ratingService.deleteAllByUser(optionalUser.get());
                userService.delete(optionalUser.get().getGoogleId());
                mailController.createAndSend(optionalUser.get().getName(), optionalUser.get().getEmail(), "This makes us feel sorry...", MailType.DELETE_ACCOUNT);
                logController.log(new Log(LogType.DELETE_ACCOUNT, null, null));
                log.info("SUCCESS");
                return user.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @GetMapping("/toRead")
    public List<BookDto> getBooksToRead(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'toRead'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK TO READ");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
            if (userOptional.isPresent()) {
                log.info("SUCCESS" + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                return bookMapper.mapToListBookDto(userOptional.get().getToRead());
            }
        }
        log.info("FAILED");
        return List.of();
    }

    @PutMapping(value = "/toRead", consumes = MediaType.APPLICATION_JSON_VALUE)
    public long addToRead(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'toRead'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK FROM 'toRead'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Book book = bookMapper.mapToBook(bookDto);
            Optional<Book> optionalBook = bookService.getByGoogleId(book.getGoogleId());
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty()) {
                    log.info("Adding new book to database...");
                    book = bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                }
                user = userService.addToRead(book, optionalUser.get());
                book = optionalBook.get();
                logController.log(new Log(LogType.ADD_TO_READ, user, book));
                log.info("SUCCESS");
                return book.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @DeleteMapping("/toRead/{googleId}")
    public long removeToRead(@PathVariable String googleId, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'toRead'");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'toRead'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty())
                    return 0;
                userService.removeToRead(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_TO_READ, optionalUser.get(), optionalBook.get()));
                log.info("SUCCESS");
                return optionalBook.get().getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @GetMapping("/during")
    public List<BookDto> getBooksReading(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'reading'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK FROM 'reading'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
            if (userOptional.isPresent()) {
                log.info("SUCCESS" + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                return bookMapper.mapToListBookDto(userOptional.get().getReading());
            }
        }
        log.info("FAILED");
        return List.of();
    }

    @PutMapping(value = "/during", consumes = MediaType.APPLICATION_JSON_VALUE)
    public long addReading(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'reading'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'reading'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Book book = bookMapper.mapToBook(bookDto);
            Optional<Book> optionalBook = bookService.getByGoogleId(book.getGoogleId());
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty()) {
                    log.info("Adding new book to database...");
                    book = bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                }
                user = userService.addReading(book, optionalUser.get());
                book = optionalBook.get();
                logController.log(new Log(LogType.ADD_DURING, user, book));
                log.info("SUCCESS");
                return book.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @DeleteMapping("/during/{googleId}")
    public long removeReading(@PathVariable String googleId, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'reading'");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'reading'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty())
                    return 0;
                userService.removeReading(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_DURING, optionalUser.get(), optionalBook.get()));
                log.info("SUCCESS");
                return optionalBook.get().getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @GetMapping("/done")
    public List<BookDto> getBooksHaveRead(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'haveRead'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK FROM 'haveRead'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
            if (userOptional.isPresent()) {
                log.info(StatusResponse.SUCCESS.toString() + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                log.info("SUCCESS");
                return bookMapper.mapToListBookDto(userOptional.get().getHaveRead());
            }
        }
        log.info("FAILED");
        return List.of();
    }

    @PutMapping("/done")
    public long addHaveRead(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'haveRead'...");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'haveRead'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Book book = bookMapper.mapToBook(bookDto);
            Optional<Book> optionalBook = bookService.getByGoogleId(book.getGoogleId());
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty()) {
                    log.info("Adding new book to database...");
                    book = bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                }
                user = userService.addHaveRead(book, optionalUser.get());
                book = optionalBook.get();
                logController.log(new Log(LogType.ADD_DONE, user, book));
                log.info("SUCCESS");
                return book.getId();
            }
        }
        log.info("FAILED");
        return 0;
    }

    @DeleteMapping("/done/{googleId}")
    public long removeHaveRead(@PathVariable String googleId, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'HaveRead'");
        User user = null;
        try {
            user = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'haveRead'");
            e.printStackTrace();
        }
        if (user != null) {
            Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
            Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
            if (optionalUser.isPresent()) {
                if (optionalBook.isEmpty())
                    return 0;
                userService.removeHaveRead(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_DONE, optionalUser.get(), optionalBook.get()));
                log.info("SUCCESS");
                return optionalBook.get().getId();
            }
        }
        log.info("FAILED");
        return 0;
    }
}
