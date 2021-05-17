package com.mikolajczyk.redude.backend.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.repository.UserRepository;
import com.mikolajczyk.redude.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTestSuite {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSave() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        when(userRepository.save(any())).thenReturn(user);

        //When
        User result = userService.save(user);

        //Then
        assertEquals(1L, result.getId());
        assertEquals("name1", result.getName());
        assertEquals("lastname1", result.getLastname());
        assertEquals("email1", result.getEmail());
        assertEquals("url1", result.getPictureUrl());
    }

    @Test
    public void testGetObservers() {
        //Given
        List<User> userList = List.of(new User(1L, "name1", "lastname1", "email1", "url1"));
        when(userRepository.getObservers()).thenReturn(userList);

        //When
        List<User> result = userService.getObservers();

        //Then
        assertEquals(result.size(), 1);
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetById() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //When
        Optional<User> optionalUser = userService.getById(1L);

        //Then
        assertTrue(optionalUser.isPresent());
        assertEquals(1L, optionalUser.get().getId());
        assertEquals("name1", optionalUser.get().getName());
        assertEquals("lastname1", optionalUser.get().getLastname());
        assertEquals("email1", optionalUser.get().getEmail());
        assertEquals("url1", optionalUser.get().getPictureUrl());
    }

    @Test
    public void testGetByGoogleId() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        when(userRepository.findByGoogleId("googleId")).thenReturn(Optional.of(user));

        //When
        Optional<User> optionalUser = userService.getByGoogleId("googleId");

        //Then
        assertTrue(optionalUser.isPresent());
        assertEquals(1L, optionalUser.get().getId());
        assertEquals("name1", optionalUser.get().getName());
        assertEquals("lastname1", optionalUser.get().getLastname());
        assertEquals("email1", optionalUser.get().getEmail());
        assertEquals("url1", optionalUser.get().getPictureUrl());
    }

    @Test
    public void testAddToRead() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        when(userRepository.save(any())).thenReturn(user);

        //When
        User result = userService.addToRead(book, user);

        //Then
        assertEquals(1L, result.getId());
        assertTrue(user.getToRead().contains(book));
    }

    @Test
    public void testRemoveToRead() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        user.addBookToRead(book);
        when(userRepository.save(any())).thenReturn(user);

        //When
        userService.removeToRead(book, user);

        //Then
        assertFalse(user.getToRead().contains(book));
    }

    @Test
    public void testAddReading() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        when(userRepository.save(any())).thenReturn(user);

        //When
        User result = userService.addReading(book, user);

        //Then
        assertEquals(1L, result.getId());
        assertTrue(user.getReading().contains(book));
    }

    @Test
    public void testRemoveReading() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        user.addBookReading(book);
        when(userRepository.save(any())).thenReturn(user);

        //When
        userService.removeReading(book, user);

        //Then
        assertFalse(user.getReading().contains(book));
    }

    @Test
    public void testAddHaveRead() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        when(userRepository.save(any())).thenReturn(user);

        //When
        User result = userService.addHaveRead(book, user);

        //Then
        assertEquals(1L, result.getId());
        assertTrue(user.getHaveRead().contains(book));
    }

    @Test
    public void testRemoveHaveRead() {
        //Given
        User user = new User(1L, "name1", "lastname1", "email1", "url1");
        Book book = new Book(10L,"isbn1","Title1", "Author1", "Categories1");
        user.addBookHaveRead(book);
        when(userRepository.save(any())).thenReturn(user);

        //When
        userService.removeHaveRead(book, user);

        //Then
        assertFalse(user.getHaveRead().contains(book));
    }

    @Test
    public void testDelete() {
        //Given
        doNothing().when(userRepository).deleteByGoogleId("googleId");

        //When & Then
        userService.delete("googleId");
    }
}
