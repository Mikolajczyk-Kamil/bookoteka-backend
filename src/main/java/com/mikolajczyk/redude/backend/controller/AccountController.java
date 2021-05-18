package com.mikolajczyk.redude.backend.controller;

import com.mikolajczyk.redude.backend.controller.response.LoginResponse;
import com.mikolajczyk.redude.backend.controller.response.StatusResponse;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.dto.UserDto;
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
    private final UserMapper userMapper;
    private final TokenVerifier verifier;
    private final RatingService ratingService;
    private final LogController logController;
    private final MailController mailController;

    @PostMapping
    public LoginResponse signIn(@RequestHeader("Authorization") String token) {
        log.info("Signing in...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                if (optionalUser.isEmpty()) {
                    log.info("User not found. Sign up new user...");
                    user.setObserver(true);
                    User result = userService.save(user);
                    mailController.createAndSend(user.getName(), user.getEmail(), "Welcome to Redudo!", MailType.WELCOME);
                    logController.log(new Log(LogType.SIGN_UP, result, null));
                    return LoginResponse.SUCCESS_NEW_USER;
                }
                log.info(LoginResponse.SUCCESS.toString());
                logController.log(new Log(LogType.SIGN_IN, optionalUser.get(), null));
                return LoginResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE SIGNING IN USER");
            e.printStackTrace();
        }
        return LoginResponse.FAILED;
    }

    @DeleteMapping
    public StatusResponse delete(@RequestHeader("Authorization") String token) {
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                logController.deleteAllUserLog(optionalUser.get());
                ratingService.deleteAllByUser(optionalUser.get());
                userService.delete(user.getGoogleId());
                mailController.createAndSend(optionalUser.get().getName(), optionalUser.get().getEmail(), "This makes us feel sorry...", MailType.DELETE_ACCOUNT);
                logController.log(new Log(LogType.DELETE_ACCOUNT, null, null));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE DELETING USER");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @GetMapping("/toRead")
    public List<BookDto> getBooksToRead(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'toRead'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
                if (userOptional.isPresent()) {
                    log.info(StatusResponse.SUCCESS.toString() + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                    return bookMapper.mapToListBookDto(userOptional.get().getToRead());
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK TO READ");
            e.printStackTrace();
        }
        return null;
    }

    @PutMapping("/toRead")
    public StatusResponse addToRead(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'toRead'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Book book = bookMapper.mapToBook(bookDto);
                Optional<Book> optionalBook = bookService.getByGoogleId(book.getGoogleId());
                if (optionalBook.isEmpty())
                    bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                optionalBook = bookService.getByGoogleId(book.getGoogleId());
                book.setId(optionalBook.get().getId());
                userService.addToRead(book, optionalUser.get());
                logController.log(new Log(LogType.ADD_TO_READ, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK FROM 'toRead'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @DeleteMapping("/toRead/{googleId}")
    public StatusResponse removeToRead(@PathVariable String googleId, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'toRead'");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByGoogleId(googleId);
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                userService.removeToRead(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_TO_READ, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'toRead'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @GetMapping("/during")
    public List<BookDto> getBooksReading(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'reading'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
                if (userOptional.isPresent()) {
                    log.info(StatusResponse.SUCCESS.toString() + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                    return bookMapper.mapToListBookDto(userOptional.get().getReading());
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK FROM 'reading'");
            e.printStackTrace();
        }
        return null;
    }

    @PutMapping("/during")
    public StatusResponse addReading(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'reading'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Book book = bookMapper.mapToBook(bookDto);
                Optional<Book> optionalBook = bookService.getByGoogleId(book.getGoogleId());
                if (optionalBook.isEmpty())
                    bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                optionalBook = bookService.getByGoogleId(book.getGoogleId());
                book.setId(optionalBook.get().getId());
                userService.addReading(book, optionalUser.get());
                logController.log(new Log(LogType.ADD_DURING, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'reading'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @DeleteMapping("/during/{isbn}")
    public StatusResponse removeReading(@PathVariable String isbn, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'reading'");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByIsbn(isbn);
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                userService.removeReading(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_DURING, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'reading'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @GetMapping("/done")
    public List<BookDto> getBooksHaveRead(@RequestHeader("Authorization") String token) {
        log.info("Trying to get books from 'haveRead'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> userOptional = userService.getByGoogleId(user.getGoogleId());
                if (userOptional.isPresent()) {
                    log.info(StatusResponse.SUCCESS.toString() + " | User(GOOGLE_ID: " + user.getGoogleId() + ")");
                    return bookMapper.mapToListBookDto(userOptional.get().getHaveRead());
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            log.info("ERROR WHILE GETTING BOOK FROM 'haveRead'");
            e.printStackTrace();
        }
        return null;
    }

    @PutMapping("/done")
    public StatusResponse addHaveRead(@RequestBody BookDto bookDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to add book 'haveRead'...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Book book = bookMapper.mapToBook(bookDto);
                Optional<Book> optionalBook = bookService.getByIsbn(book.getGoogleId());
                if (optionalBook.isEmpty())
                    bookService.saveOrUpdate(bookMapper.mapToBook(bookDto));
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                optionalBook = bookService.getByIsbn(book.getGoogleId());
                book.setId(optionalBook.get().getId());
                userService.addHaveRead(book, optionalUser.get());
                logController.log(new Log(LogType.ADD_DONE, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'haveRead'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }

    @DeleteMapping("/done/{isbn}")
    public StatusResponse removeHaveRead(@PathVariable String isbn, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'HaveRead'");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByIsbn(isbn);
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                userService.removeHaveRead(optionalBook.get(), optionalUser.get());
                logController.log(new Log(LogType.REMOVE_DONE, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'haveRead'");
            e.printStackTrace();
        }
        return StatusResponse.VERIFICATION_FAILED;
    }
}
