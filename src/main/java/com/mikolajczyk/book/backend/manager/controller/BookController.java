package com.mikolajczyk.book.backend.manager.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.book.backend.manager.controller.response.StatusResponse;
import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.domain.User;
import com.mikolajczyk.book.backend.manager.mapper.BookMapper;
import com.mikolajczyk.book.backend.manager.controller.response.ApiResponse;
import com.mikolajczyk.book.backend.manager.service.BookService;
import com.mikolajczyk.book.backend.manager.sources.googleBooks.service.ExtendedSearchService;
import com.mikolajczyk.book.backend.manager.verifier.TokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BookService service;
    private final BookMapper mapper;
    private final ExtendedSearchService extendedSearchService;

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
                    return new ApiResponse(mapper.mapToListBookDto(extendedSearchService.getBooksByQuery(q, external)), StatusResponse.SUCCESS);
                else if (title != null && author != null && category != null)
                    return new ApiResponse(mapper.mapToListBookDto(service.getAllByTitleAndAuthorAndCategory(title, author, category)), StatusResponse.SUCCESS);
                else if (title != null && author != null)
                    return new ApiResponse(mapper.mapToListBookDto(extendedSearchService.getBooksByTitleAndAuthor(title, author, external)), StatusResponse.SUCCESS);
                else if (title != null && category != null)
                    return new ApiResponse(mapper.mapToListBookDto(service.getAllByTitleAndCategory(title, category)), StatusResponse.SUCCESS);
                else if (author != null && category != null)
                    return new ApiResponse(mapper.mapToListBookDto(service.getAllByAuthorAndCategory(author, category)), StatusResponse.SUCCESS);
                else if (title != null)
                    return new ApiResponse(mapper.mapToListBookDto(extendedSearchService.getBooksByTitle(title, external)), StatusResponse.SUCCESS);
                else if (author != null)
                    return new ApiResponse(mapper.mapToListBookDto(extendedSearchService.getBooksByAuthor(author, external)), StatusResponse.SUCCESS);
                else if (category != null)
                    return new ApiResponse(mapper.mapToListBookDto(service.getAllByCategory(category)), StatusResponse.SUCCESS);
                else if (isbn != null){
                    Optional<Book> result = extendedSearchService.getBookByIsbn(isbn);
                    List<Book> list = new ArrayList<>();
                    result.ifPresent(list::add);
                    return new ApiResponse(mapper.mapToListBookDto(list), StatusResponse.SUCCESS);
                }
            }
        } catch (GeneralSecurityException | IOException | UnirestException e) {
            e.printStackTrace();
        }
        return new ApiResponse(null, StatusResponse.VERIFICATION_FAILED);
    }
}
