package com.mikolajczyk.redude.backend.controller;

import com.google.gson.Gson;
import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.dto.UserDto;
import com.mikolajczyk.redude.backend.log.LogController;
import com.mikolajczyk.redude.backend.mail.controller.MailController;
import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.mapper.UserMapper;
import com.mikolajczyk.redude.backend.rating.service.RatingService;
import com.mikolajczyk.redude.backend.service.BookService;
import com.mikolajczyk.redude.backend.service.UserService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@WebMvcTest(AccountController.class)
public class AccountControllerTestSuite {

    private final String apiRoot = "/v1/account";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private UserService userService;
    @MockBean
    private BookMapper bookMapper;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private TokenVerifier verifier;
    @MockBean
    private RatingService ratingService;
    @MockBean
    private LogController logController;
    @MockBean
    private MailController mailController;


    @Test
    public void testSingInShouldFetchSuccessNewUserLogin() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(anyString())).thenReturn(user);
        when(userService.getByGoogleId(anyString())).thenReturn(Optional.empty());
        when(userService.save(any())).thenReturn(user);
        doNothing().when(mailController).createAndSend(any(), any(), any(), any());
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.post(apiRoot).header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS_NEW_USER")));
    }

    @Test
    public void testSingInShouldFetchSuccessLogin() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(anyString())).thenReturn(user);
        when(userService.getByGoogleId(anyString())).thenReturn(Optional.of(user));
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.post(apiRoot).header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testSingInShouldFetchFailedLogin() throws Exception {
        //Given
        when(verifier.verify(anyString())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.post(apiRoot).header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("FAILED")));
    }

    @Test
    public void testUpdateShouldFetchUserDto() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        UserDto userDto = new UserDto(1L, "name1", "lastname1", "email1", "pictureUrl1");
        String jsonContent = new Gson().toJson(userDto);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        doNothing().when(logController).log(any());
        when(userMapper.mapToUserDto(any())).thenReturn(userDto);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot)
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname", Matchers.is("lastname1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("email1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pictureUrl", Matchers.is("pictureUrl1")));
    }

    @Test
    public void testDeleteShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        doNothing().when(logController).log(any());
        doNothing().when(logController).deleteAllUserLog(any());
        doNothing().when(ratingService).deleteAllByUser(any());
        doNothing().when(userService).delete(any());
        doNothing().when(mailController).createAndSend(any(), any(), any(), any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot)
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testDeleteShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot)
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testDeleteShouldFetchVerificationFailed() throws Exception {
        //Given
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot)
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testGetBooksToReadShouldFetchListBookDto() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        BookDto bookDto1 = new BookDto("googleId1", "title1", "author1");
        BookDto bookDto2 = new BookDto("googleId2", "title2", "author2");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToListBookDto(any())).thenReturn(List.of(bookDto1, bookDto2));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.get(apiRoot + "/toRead")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].googleId", Matchers.is("googleId1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", Matchers.is("title1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].author", Matchers.is("author1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].googleId", Matchers.is("googleId2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title", Matchers.is("title2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].author", Matchers.is("author2")));
    }

    @Test
    public void testAddToReadShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        when(userService.addToRead(any(), any())).thenReturn(user);
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/toRead")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testAddToReadShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/toRead")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testAddToReadShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/toRead")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testAddToReadShouldFetchVerificationFailed() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/toRead")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testRemoveToReadShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        doNothing().when(userService).removeToRead(any(), any());
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/toRead/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testRemoveToReadShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/toRead/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testRemoveToReadShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/toRead/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testRemoveToReadShouldFetchVerificationFailed() throws Exception {
        //Given
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/toRead/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testGetBooksReadingShouldFetchListBookDto() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        BookDto bookDto1 = new BookDto("googleId1", "title1", "author1");
        BookDto bookDto2 = new BookDto("googleId2", "title2", "author2");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToListBookDto(any())).thenReturn(List.of(bookDto1, bookDto2));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.get(apiRoot + "/during")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].googleId", Matchers.is("googleId1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", Matchers.is("title1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].author", Matchers.is("author1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].googleId", Matchers.is("googleId2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title", Matchers.is("title2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].author", Matchers.is("author2")));
    }

    @Test
    public void testAddReadingShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        when(userService.addReading(any(), any())).thenReturn(user);
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/during")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testAddReadingShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/during")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testAddReadingShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/during")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testAddReadingShouldFetchVerificationFailed() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/during")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testRemoveReadingShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        doNothing().when(userService).removeReading(any(), any());
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/during/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testRemoveReadingShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/during/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testRemoveReadingShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/during/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testRemoveReadingShouldFetchVerificationFailed() throws Exception {
        //Given
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/during/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testGetBooksHaveReadShouldFetchListBookDto() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        BookDto bookDto1 = new BookDto("googleId1", "title1", "author1");
        BookDto bookDto2 = new BookDto("googleId2", "title2", "author2");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToListBookDto(any())).thenReturn(List.of(bookDto1, bookDto2));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.get(apiRoot + "/done")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].googleId", Matchers.is("googleId1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", Matchers.is("title1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].author", Matchers.is("author1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].googleId", Matchers.is("googleId2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title", Matchers.is("title2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].author", Matchers.is("author2")));
    }

    @Test
    public void testAddHaveReadShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        when(userService.addHaveRead(any(), any())).thenReturn(user);
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/done")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testAddHaveReadShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/done")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testAddHaveReadShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookMapper.mapToBook(any())).thenReturn(book);
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/done")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testAddHaveReadShouldFetchVerificationFailed() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        String jsonContent = new Gson().toJson(book);
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.put(apiRoot + "/done")
                        .header("Authorization", "token")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }

    @Test
    public void testRemoveHaveReadShouldFetchSuccess() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));
        doNothing().when(userService).removeHaveRead(any(), any());
        doNothing().when(logController).log(any());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/done/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("SUCCESS")));
    }

    @Test
    public void testRemoveHaveReadShouldFetchBookNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.of(user));
        when(bookService.getByIsbn(any())).thenReturn(Optional.empty());

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/done/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("BOOK_NOT_FOUND")));
    }

    @Test
    public void testRemoveHaveReadShouldFetchUserNotFound() throws Exception {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");
        Book book = new Book(2L, "isbn1", "title1", "author1", "categories1");
        when(verifier.verify(any())).thenReturn(user);
        when(userService.getByGoogleId(any())).thenReturn(Optional.empty());
        when(bookService.getByIsbn(any())).thenReturn(Optional.of(book));

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/done/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("USER_NOT_FOUND")));
    }

    @Test
    public void testRemoveHaveReadShouldFetchVerificationFailed() throws Exception {
        //Given
        when(verifier.verify(any())).thenReturn(null);

        //When & Then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(apiRoot + "/done/isbn1")
                        .header("Authorization", "token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is("VERIFICATION_FAILED")));
    }
}
