package com.mikolajczyk.redude.backend.repository;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookRepositoryTestSuite {

    @Autowired
    private BookService service;

    @Test
    public void testSaveAndDeleteBook() {
        //Given
        Book book = new Book(
                "googleId1",
                "1234",
                "",
                "title1",
                "author1",
                "desc1",
                "publisher1",
                "2020",
                "fiction",
                "coverUrl",
                "12.45",
                "ebookUrl");
        //When
        Book result = service.saveOrUpdate(book);

        //Then
        assertEquals(result.getIsbn(), book.getIsbn());
        //CleanUp
        service.delete(result);
    }

    @Test
    public void testGetByTitle() {
        //Given
        Book book = new Book(
                "googleId1",
                "1234",
                "",
                "title1",
                "author1, author2",
                "desc1",
                "publisher1",
                "2020",
                "fiction",
                "coverUrl",
                "12.45",
                "ebookUrl");
        //When
        Book resultOfSave = service.saveOrUpdate(book);
        List<Book> result = service.getAllByTitle("tITl");
        System.out.println(result.get(0).getAuthor());
        //Then
        assertTrue(result.size() >= 1);
        assertTrue(result.get(0).getTitle().contains("tit"));
        //CleanUp
        service.delete(resultOfSave);
    }

    @Test
    public void testFindByCategory() {
        //Given
        Book book = new Book(
                "googleId1",
                "1234",
                "",
                "title1",
                "author1",
                "desc1",
                "publisher1",
                "2020",
                "fiction",
                "coverUrl",
                "12.45",
                "ebookUrl");

        Book book2 = new Book(
                "googleId2",
                "12345",
                "",
                "title2",
                "author2",
                "desc2",
                "publisher2",
                "2020",
                "fiction",
                "coverUrl2",
                "15.45",
                "ebookUrl2");

        //Given
        Book book3 = new Book(
                "googleId3",
                "12343",
                "",
                "title3",
                "author3",
                "desc3",
                "publisher3",
                "2020",
                "fiction, horror",
                "coverUrl3",
                "13.45",
                "ebookUrl3");

        //When
        service.saveOrUpdate(book);
        service.saveOrUpdate(book2);
        service.saveOrUpdate(book3);

        List<Book> result = service.getAllByCategory("fiction");

        //Then
        assertTrue(result.size() >= 2);
        assertTrue(result.get(0).getCategories().contains("fiction"));
        assertTrue(result.get(1).getCategories().contains("fiction"));

        //CleanUp
        service.delete(book);
        service.delete(book2);
        service.delete(book3);
    }
}
