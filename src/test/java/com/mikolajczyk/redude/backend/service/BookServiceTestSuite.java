package com.mikolajczyk.redude.backend.service;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.repository.BookRepository;
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
public class BookServiceTestSuite {

    @Autowired
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void testSaveOrUpdate() {
        //Given
        Book book = new Book(10L,"googleId1","Title1", "Author1", "Categories1");
        Optional<Book> optionalBook = Optional.of(book);
        when(bookRepository.findByGoogleId(any())).thenReturn(optionalBook);
        when(bookRepository.save(any())).thenReturn(book);

        //When
        Book result = bookService.saveOrUpdate(book);

        //Then
        assertEquals(10L, result.getId());
        assertEquals("googleId1", result.getGoogleId());
        assertEquals("Title1", result.getTitle());
        assertEquals("Author1", result.getAuthor());
        assertEquals("categories1", result.getCategories());
    }

    @Test
    public void testGetByIsbn() {
        //Given
        Optional<Book> optionalBook = Optional.of(new Book(1L,"googleId1","Title1", "Author1", "Categories1"));
        when(bookRepository.findByIsbn(any())).thenReturn(optionalBook);

        //When
        Optional<Book> result = bookService.getByIsbn("isbn1");

        //Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("googleId1", result.get().getGoogleId());
        assertEquals("Title1", result.get().getTitle());
        assertEquals("Author1", result.get().getAuthor());
        assertEquals("Categories1", result.get().getCategories());
    }

    @Test
    public void testGetByGoogleId() {
        //Given
        Optional<Book> optionalBook = Optional.of(new Book(1L,"googleId1","Title1", "Author1", "Categories1"));
        when(bookRepository.findByGoogleId(any())).thenReturn(optionalBook);

        //When
        Optional<Book> result = bookService.getByGoogleId("googleId1");

        //Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("googleId1", result.get().getGoogleId());
        assertEquals("Title1", result.get().getTitle());
        assertEquals("Author1", result.get().getAuthor());
        assertEquals("Categories1", result.get().getCategories());
    }

    @Test
    public void testGetAllByTitle() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByTitle("Title")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByTitle("Title");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByAuthor() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByAuthor("Author1")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByAuthor("Author1");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByCategory() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByCategory("Category")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByCategory("Category");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByQ() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByQ("tit")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByQ("tit");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByTitleAuthorCategory() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByTitleAndAuthorAndCategory("title", "author", "category")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByTitleAndAuthorAndCategory("title", "author", "category");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByTitleAuthor() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByTitleAndAuthor("title", "author")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByTitleAndAuthor("title", "author");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByTitleCategory() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByTitleAndCategory("title",  "category")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByTitleAndCategory("title",  "category");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testGetAllByAuthorCategory() {
        //Given
        Book book1 = new Book(1L,"googleId1","Title1", "Author1", "Categories1");
        Book book2 = new Book(2L,"googleId2","Title2", "Author2", "Categories2");
        List<Book> booksList = List.of(book1, book2);
        when(bookRepository.findAllByAuthorAndCategory("author", "category")).thenReturn(booksList);

        //When
        List<Book> result = bookService.getAllByAuthorAndCategory("author", "category");

        //Then
        assertTrue(result.size() > 1);
        assertEquals(1L, result.get(0).getId());
        assertEquals("googleId1", result.get(0).getGoogleId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Author1", result.get(0).getAuthor());
        assertEquals("Categories1", result.get(0).getCategories());
        assertEquals(2L, result.get(1).getId());
        assertEquals("googleId2", result.get(1).getGoogleId());
        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Author2", result.get(1).getAuthor());
        assertEquals("Categories2", result.get(1).getCategories());
    }

    @Test
    public void testDelete() {
        //Given
        doNothing().when(bookRepository).delete(any());

        //When & Then
        bookService.delete(new Book());
    }
}
