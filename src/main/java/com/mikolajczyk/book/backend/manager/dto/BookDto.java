package com.mikolajczyk.book.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Getter
@Setter
public class BookDto {

    private long id;
    private String googleId;
    private String isbn;
    private String industryId;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String published;
    private String categories;
    private String coverUrl;
    private String priceEbook;
    private String ebookUrl;

    public BookDto(String googleId, String isbn, String industryId, String title, String author, String description, String publisher, String published, String categories, String coverUrl, String priceEbook, String ebookUrl) {
        this.googleId = googleId;
        this.isbn = isbn;
        this.industryId = industryId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.published = published;
        this.categories = categories;
        this.coverUrl = coverUrl;
        this.priceEbook = priceEbook;
        this.ebookUrl = ebookUrl;
    }
}
