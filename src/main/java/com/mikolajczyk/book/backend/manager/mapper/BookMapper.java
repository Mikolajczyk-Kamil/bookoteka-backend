package com.mikolajczyk.book.backend.manager.mapper;

import com.mikolajczyk.book.backend.manager.domain.Book;
import com.mikolajczyk.book.backend.manager.dto.BookDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookDto mapToBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getGoogleId(),
                book.getIsbn(),
                book.getIndustryId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPublisher(),
                book.getPublished(),
                book.getCategories(),
                book.getCoverUrl(),
                book.getPriceEbook(),
                book.getEbookUrl()
        );
    }

    public Book mapToBook(BookDto bookDto) {
        return new Book(
                bookDto.getGoogleId(),
                bookDto.getIsbn(),
                bookDto.getIndustryId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getDescription(),
                bookDto.getPublisher(),
                bookDto.getPublished(),
                bookDto.getCategories(),
                bookDto.getCoverUrl(),
                bookDto.getPriceEbook(),
                bookDto.getEbookUrl()
        );
    }

    public List<BookDto> mapToListBookDto(List<Book> bookList) {
        return bookList.stream().map(this::mapToBookDto).collect(Collectors.toList());
    }

    public List<Book> mapToListBook(List<BookDto> bookListDto) {
        return bookListDto.stream().map(this::mapToBook).collect(Collectors.toList());
    }
}
