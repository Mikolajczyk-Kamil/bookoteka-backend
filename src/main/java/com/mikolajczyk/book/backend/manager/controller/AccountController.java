package com.mikolajczyk.book.backend.manager.controller;

import com.mikolajczyk.book.backend.manager.controller.response.LoginResponse;
import com.mikolajczyk.book.backend.manager.controller.response.StatusResponse;
import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.domain.User;
import com.mikolajczyk.book.backend.manager.dto.BookDto;
import com.mikolajczyk.book.backend.manager.dto.UserDto;
import com.mikolajczyk.book.backend.manager.log.domain.Log;
import com.mikolajczyk.book.backend.manager.log.service.LogService;
import com.mikolajczyk.book.backend.manager.log.type.LogType;
import com.mikolajczyk.book.backend.manager.mapper.BookMapper;
import com.mikolajczyk.book.backend.manager.mapper.UserMapper;
import com.mikolajczyk.book.backend.manager.service.BookService;
import com.mikolajczyk.book.backend.manager.service.UserService;
import com.mikolajczyk.book.backend.manager.verifier.TokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LogService logService;

    @PostMapping
    public LoginResponse signIn(@RequestHeader("Authorization") String token) {
        log.info("Signing in...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                if (optionalUser.isEmpty()) {
                    log.info("User not found. Sign up new user...");
                    User result = userService.save(user);
                    logService.save(new Log(LogType.SIGN_UP, result, null));
                    return LoginResponse.SUCCESS_NEW_USER;
                }
                log.info(LoginResponse.SUCCESS.toString());
                logService.save(new Log(LogType.SIGN_IN, optionalUser.get(), null));
                return LoginResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE SIGNING IN USER");
            e.printStackTrace();
        }
        return LoginResponse.FAILED;
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token) {
        log.info("Trying to update account...");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                log.info("Updating user...");
                Optional<User> result = userService.getByGoogleId(userDto.getGoogleId());
                if (result.isEmpty())
                    return null;
                userDto.setId(result.get().getId());
                log.info(StatusResponse.SUCCESS.toString());
                logService.save(new Log(LogType.UPDATE_ACCOUNT, result.get(), null));
                return userMapper.mapToUserDto(userService.save(userMapper.mapToUser(userDto)));
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE UPDATING USER(GOOGLE_ID: " + userDto.getGoogleId() + ")");
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping
    public StatusResponse delete(@RequestHeader("Authorization") String token) {
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                userService.delete(user.getGoogleId());
                logService.save(new Log(LogType.DELETE_ACCOUNT, user, null));
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
                Optional<Book> optionalBook = bookService.getByIsbn(book.getIsbn());
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                book.setId(optionalBook.get().getId());
                userService.addToRead(book, optionalUser.get());
                logService.save(new Log(LogType.ADD_TO_READ, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK FROM 'toRead'");
            e.printStackTrace();
        }
        return StatusResponse.FAILED;
    }

    @DeleteMapping("/toRead/{isbn}")
    public StatusResponse removeToRead(@PathVariable String isbn, @RequestHeader("Authorization") String token) {
        log.info("Trying to remove from 'toRead'");
        try {
            User user = verifier.verify(token);
            if (user != null) {
                Optional<User> optionalUser = userService.getByGoogleId(user.getGoogleId());
                Optional<Book> optionalBook = bookService.getByIsbn(isbn);
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                userService.removeToRead(optionalBook.get(), optionalUser.get());
                logService.save(new Log(LogType.REMOVE_TO_READ, optionalUser.get(), optionalBook.get()));
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
                Optional<Book> optionalBook = bookService.getByIsbn(book.getIsbn());
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                book.setId(optionalBook.get().getId());
                userService.addReading(book, optionalUser.get());
                logService.save(new Log(LogType.ADD_DURING, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'reading'");
            e.printStackTrace();
        }
        return StatusResponse.FAILED;
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
                logService.save(new Log(LogType.REMOVE_DURING, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'reading'");
            e.printStackTrace();
        }
        return StatusResponse.FAILED;
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
                Optional<Book> optionalBook = bookService.getByIsbn(book.getIsbn());
                if (optionalBook.isEmpty())
                    return StatusResponse.BOOK_NOT_FOUND;
                if (optionalUser.isEmpty())
                    return StatusResponse.USER_NOT_FOUND;
                book.setId(optionalBook.get().getId());
                userService.addHaveRead(book, optionalUser.get());
                logService.save(new Log(LogType.ADD_DONE, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE ADDING BOOK to 'haveRead'");
            e.printStackTrace();
        }
        return StatusResponse.FAILED;
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
                logService.save(new Log(LogType.REMOVE_DONE, optionalUser.get(), optionalBook.get()));
                return StatusResponse.SUCCESS;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("ERROR WHILE REMOVE BOOK FROM 'haveRead'");
            e.printStackTrace();
        }
        return StatusResponse.FAILED;
    }
}
