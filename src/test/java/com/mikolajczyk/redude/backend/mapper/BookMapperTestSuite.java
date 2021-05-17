package com.mikolajczyk.redude.backend.mapper;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookMapperTestSuite {

    @Autowired
    private BookMapper mapper;

    @Test
    public void testMapToListBookDto() {
        //Given
        Book book1 = new Book(
                "googleId1",
                "isbn1",
                "industryId1",
                "title1",
                "author1",
                "description1",
                "publisher1",
                "published1",
                "categories1",
                "coverUrl1",
                "priceBook1",
                "ebookUrl1"
        );

        Book book2 = new Book(
                "googleId2",
                "isbn2",
                "industryId2",
                "title2",
                "author2",
                "description2",
                "publisher2",
                "published2",
                "categories2",
                "coverUrl2",
                "priceBook2",
                "ebookUrl2"
        );
        List<Book> bookList = List.of(book1, book2);
        //When
        List<BookDto> bookDtoList = mapper.mapToListBookDto(bookList);

        //Then
        assertEquals(2, bookDtoList.size());
        assertAll(
                () -> assertEquals("googleId1", bookDtoList.get(0).getGoogleId()),
                () -> assertEquals("isbn1", bookDtoList.get(0).getIsbn()),
                () -> assertEquals("industryId1", bookDtoList.get(0).getIndustryId()),
                () -> assertEquals("title1", bookDtoList.get(0).getTitle()),
                () -> assertEquals("author1", bookDtoList.get(0).getAuthor()),
                () -> assertEquals("description1", bookDtoList.get(0).getDescription()),
                () -> assertEquals("publisher1", bookDtoList.get(0).getPublisher()),
                () -> assertEquals("published1", bookDtoList.get(0).getPublished()),
                () -> assertEquals("categories1", bookDtoList.get(0).getCategories()),
                () -> assertEquals("coverUrl1", bookDtoList.get(0).getCoverUrl()),
                () -> assertEquals("priceBook1", bookDtoList.get(0).getPriceEbook()),
                () -> assertEquals("ebookUrl1", bookDtoList.get(0).getEbookUrl())
        );
        assertAll(
                () -> assertEquals("googleId2", bookDtoList.get(1).getGoogleId()),
                () -> assertEquals("isbn2", bookDtoList.get(1).getIsbn()),
                () -> assertEquals("industryId2", bookDtoList.get(1).getIndustryId()),
                () -> assertEquals("title2", bookDtoList.get(1).getTitle()),
                () -> assertEquals("author2", bookDtoList.get(1).getAuthor()),
                () -> assertEquals("description2", bookDtoList.get(1).getDescription()),
                () -> assertEquals("publisher2", bookDtoList.get(1).getPublisher()),
                () -> assertEquals("published2", bookDtoList.get(1).getPublished()),
                () -> assertEquals("categories2", bookDtoList.get(1).getCategories()),
                () -> assertEquals("coverUrl2", bookDtoList.get(1).getCoverUrl()),
                () -> assertEquals("priceBook2", bookDtoList.get(1).getPriceEbook()),
                () -> assertEquals("ebookUrl2", bookDtoList.get(1).getEbookUrl())
        );
    }

    @Test
    public void testMapToBookDto() {
        //Given
        Book book = new Book(
                "googleId1",
                "isbn1",
                "industryId1",
                "title1",
                "author1",
                "description1",
                "publisher1",
                "published1",
                "categories1",
                "coverUrl1",
                "priceBook1",
                "ebookUrl1"
        );

        //When
        BookDto bookDto = mapper.mapToBookDto(book);

        //Then
        assertEquals("googleId1", bookDto.getGoogleId());
        assertEquals("isbn1", bookDto.getIsbn());
        assertEquals("industryId1", bookDto.getIndustryId());
        assertEquals("title1", bookDto.getTitle());
        assertEquals("author1", bookDto.getAuthor());
        assertEquals("description1", bookDto.getDescription());
        assertEquals("publisher1", bookDto.getPublisher());
        assertEquals("published1", bookDto.getPublished());
        assertEquals("categories1", bookDto.getCategories());
        assertEquals("coverUrl1", bookDto.getCoverUrl());
        assertEquals("priceBook1", bookDto.getPriceEbook());
        assertEquals("ebookUrl1", bookDto.getEbookUrl());
    }

    @Test
    public void testMapToListBook() {
        //Given
        BookDto bookDto1 = new BookDto(
                "googleId1",
                "isbn1",
                "industryId1",
                "title1",
                "author1",
                "description1",
                "publisher1",
                "published1",
                "categories1",
                "coverUrl1",
                "priceBook1",
                "ebookUrl1"
        );

        BookDto bookDto2 = new BookDto(
                "googleId2",
                "isbn2",
                "industryId2",
                "title2",
                "author2",
                "description2",
                "publisher2",
                "published2",
                "categories2",
                "coverUrl2",
                "priceBook2",
                "ebookUrl2"
        );
        List<BookDto> bookDtoList = List.of(bookDto1, bookDto2);
        //When
        List<Book> booksList = mapper.mapToListBook(bookDtoList);

        //Then
        assertEquals(2, booksList.size());
        assertAll(
                () -> assertEquals("googleId1", booksList.get(0).getGoogleId()),
                () -> assertEquals("isbn1", booksList.get(0).getIsbn()),
                () -> assertEquals("industryId1", booksList.get(0).getIndustryId()),
                () -> assertEquals("title1", booksList.get(0).getTitle()),
                () -> assertEquals("author1", booksList.get(0).getAuthor()),
                () -> assertEquals("description1", booksList.get(0).getDescription()),
                () -> assertEquals("publisher1", booksList.get(0).getPublisher()),
                () -> assertEquals("published1", booksList.get(0).getPublished()),
                () -> assertEquals("categories1", booksList.get(0).getCategories()),
                () -> assertEquals("coverUrl1", booksList.get(0).getCoverUrl()),
                () -> assertEquals("priceBook1", booksList.get(0).getPriceEbook()),
                () -> assertEquals("ebookUrl1", booksList.get(0).getEbookUrl())
        );
        assertAll(
                () -> assertEquals("googleId2", booksList.get(1).getGoogleId()),
                () -> assertEquals("isbn2", booksList.get(1).getIsbn()),
                () -> assertEquals("industryId2", booksList.get(1).getIndustryId()),
                () -> assertEquals("title2", booksList.get(1).getTitle()),
                () -> assertEquals("author2", booksList.get(1).getAuthor()),
                () -> assertEquals("description2", booksList.get(1).getDescription()),
                () -> assertEquals("publisher2", booksList.get(1).getPublisher()),
                () -> assertEquals("published2", booksList.get(1).getPublished()),
                () -> assertEquals("categories2", booksList.get(1).getCategories()),
                () -> assertEquals("coverUrl2", booksList.get(1).getCoverUrl()),
                () -> assertEquals("priceBook2", booksList.get(1).getPriceEbook()),
                () -> assertEquals("ebookUrl2", booksList.get(1).getEbookUrl())
        );
    }

    @Test
    public void testMapToBook() {
        //Given
        BookDto bookDto = new BookDto(
                "googleId1",
                "isbn1",
                "industryId1",
                "title1",
                "author1",
                "description1",
                "publisher1",
                "published1",
                "categories1",
                "coverUrl1",
                "priceBook1",
                "ebookUrl1"
        );

        //When
        Book book = mapper.mapToBook(bookDto);

        //Then
        assertEquals("googleId1", book.getGoogleId());
        assertEquals("isbn1", book.getIsbn());
        assertEquals("industryId1", book.getIndustryId());
        assertEquals("title1", book.getTitle());
        assertEquals("author1", book.getAuthor());
        assertEquals("description1", book.getDescription());
        assertEquals("publisher1", book.getPublisher());
        assertEquals("published1", book.getPublished());
        assertEquals("categories1", book.getCategories());
        assertEquals("coverUrl1", book.getCoverUrl());
        assertEquals("priceBook1", book.getPriceEbook());
        assertEquals("ebookUrl1", book.getEbookUrl());
    }
}
