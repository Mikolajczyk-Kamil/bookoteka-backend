package com.mikolajczyk.redude.backend.controller;

import com.google.gson.Gson;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.dto.UserDto;
import com.mikolajczyk.redude.backend.log.LogController;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.rating.dto.RatingDto;
import com.mikolajczyk.redude.backend.rating.mapper.RatingMapper;
import com.mikolajczyk.redude.backend.rating.service.RatingService;
import com.mikolajczyk.redude.backend.service.BookService;
import com.mikolajczyk.redude.backend.service.UserService;
import com.mikolajczyk.redude.backend.sources.googleBooks.service.ExtendedSearchService;
import com.mikolajczyk.redude.backend.verifier.TokenVerifier;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@WebMvcTest(BookController.class)
public class BookControllerTestSuite {

    private final String apiRoot = "/v1/books";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenVerifier verifier;
    @MockBean
    private UserService userService;
    @MockBean
    private BookService bookService;
    @MockBean
    private BookMapper bookMapper;
    @MockBean
    private RatingMapper ratingMapper;
    @MockBean
    private RatingService ratingService;
    @MockBean
    private ExtendedSearchService extendedSearchService;
    @MockBean
    private LogController logController;

    @Test
    public void testRateBookShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        Rating rating = new Rating(user, book, 10, "comment1");
        String jsonContent = new Gson().toJson(rating);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByGoogleId(any())).thenReturn(Optional.of(book));
        when(ratingMapper.mapToRating(any())).thenReturn(rating);
        when(ratingService.save(any())).thenReturn(rating);
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(apiRoot + "/googleId1")
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testRateBookShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        Rating rating = new Rating(user, book, 10, "comment1");
        String jsonContent = new Gson().toJson(rating);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByGoogleId(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(apiRoot + "/googleId1")
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testRateBookShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        Rating rating = new Rating(user, book, 10, "comment1");
        String jsonContent = new Gson().toJson(rating);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookService.getByGoogleId(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(apiRoot + "/googleId1")
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testRateBookShouldFetchVerificationFailed() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        Rating rating = new Rating(user, book, 10, "comment1");
        String jsonContent = new Gson().toJson(rating);
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(apiRoot + "/googleId1")
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testDeleteRatingShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "googleId1", "isbn1", "title1", "author1", "categories1");
        BookDto bookDto = new BookDto("googleId1", "title1", "author1");
        Rating rating = new Rating(user, book, 10, "comment1");
        RatingDto ratingDto = new RatingDto(userDto, bookDto, 10, "comment1");
        String jsonContent = new Gson().toJson(ratingDto);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByGoogleId(any())).thenReturn(Optional.of(book));
        when(ratingService.getById(anyLong())).thenReturn(Optional.of(rating));
        doNothing().when(ratingService).delete(any());
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(apiRoot)
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testDeleteRatingShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "googleId1", "isbn1", "title1", "author1", "categories1");
        BookDto bookDto = new BookDto("googleId1", "title1", "author1");
        RatingDto ratingDto = new RatingDto(userDto, bookDto, 10, "comment1");
        String jsonContent = new Gson().toJson(ratingDto);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByGoogleId(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(apiRoot)
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testDeleteRatingShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "googleId1", "isbn1", "title1", "author1", "categories1");
        BookDto bookDto = new BookDto("googleId1", "title1", "author1");
        RatingDto ratingDto = new RatingDto(userDto, bookDto, 10, "comment1");
        String jsonContent = new Gson().toJson(ratingDto);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookService.getByGoogleId(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(apiRoot)
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testDeleteRatingShouldFetchFailed() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "googleId1", "isbn1", "title1", "author1", "categories1");
        BookDto bookDto = new BookDto("googleId1", "title1", "author1");
        RatingDto ratingDto = new RatingDto(userDto, bookDto, 10, "comment1");
        String jsonContent = new Gson().toJson(ratingDto);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByGoogleId(any())).thenReturn(Optional.of(book));
        when(ratingService.getById(anyLong())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(apiRoot)
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("FAILED")));
    }

    @Test
    public void testDeleteRatingShouldFetchVerificationFailed() throws Exception {
        //Given
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        BookDto bookDto = new BookDto("googleId1", "title1", "author1");
        RatingDto ratingDto = new RatingDto(userDto, bookDto, 10, "comment1");
        String jsonContent = new Gson().toJson(ratingDto);
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(apiRoot)
                .header("Authorization", "token")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }
}
