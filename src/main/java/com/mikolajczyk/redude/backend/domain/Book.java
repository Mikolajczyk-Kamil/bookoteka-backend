package com.mikolajczyk.redude.backend.domain;

import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "Book.findAllByTitleAndAuthorAndCategory",
                query = "FROM Book WHERE title LIKE CONCAT('%', :TITLE, '%') AND author LIKE CONCAT('%', :AUTHOR, '%') AND categories LIKE CONCAT('%', :CATEGORY, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByTitleAndAuthor",
                query = "FROM Book WHERE title LIKE CONCAT('%', :TITLE, '%') AND author LIKE CONCAT('%', :AUTHOR, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByTitleAndCategory",
                query = "FROM Book WHERE title LIKE CONCAT('%', :TITLE, '%') AND categories LIKE CONCAT('%', :CATEGORY, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByAuthorAndCategory",
                query = "FROM Book WHERE author LIKE CONCAT('%', :AUTHOR, '%') AND categories LIKE CONCAT('%', :CATEGORY, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByQ",
                query = "FROM Book WHERE title LIKE CONCAT('%', :Q, '%') AND author LIKE CONCAT('%', :Q, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByCategory",
                query = "FROM Book WHERE categories LIKE CONCAT('%', :CATEGORY, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByTitle",
                query = "FROM Book WHERE title LIKE CONCAT('%', :TITLE, '%')"
        ),
        @NamedQuery(
                name = "Book.findAllByAuthor",
                query = "FROM Book WHERE author LIKE CONCAT('%', :AUTHOR, '%')"
        )
})
@NoArgsConstructor
@Entity
@Table(name = "BOOKS")
public class Book {

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
    private List<User> toReadUsers = new ArrayList<>();
    private List<User> readingUsers = new ArrayList<>();
    private List<User> haveReadUsers = new ArrayList<>();
    private List<Log> logs = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    public Book(String googleId, String isbn, String industryId, String title, String author, String description, String publisher, String published, String categories, String coverUrl, String priceEbook, String ebookUrl) {
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

    public Book(long id, String googleId, String title, String author, String categories) {
        this.id = id;
        this.googleId = googleId;
        this.title = title;
        this.author = author;
        this.categories = categories;
    }

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", unique = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    @Column(name = "GOOGLE_ID", unique = true)
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @Column(name = "ISBN", unique = true)
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Column(name = "INDUSTRY_ID")
    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    @NotNull
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Column(name = "AUTHOR")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "DESCRIPTION", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "PUBLISHER")
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Column(name = "PUBLISHED")
    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    @Column(name = "CATEGORIES")
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    @Column(name = "COVER_URL")
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String cover) {
        this.coverUrl = cover;
    }

    @Column(name = "EBOOK_PRICE")
    public String getPriceEbook() {
        return priceEbook;
    }

    public void setPriceEbook(String priceEbook) {
        this.priceEbook = priceEbook;
    }

    @Column(name = "EBOOK_URL")
    public String getEbookUrl() {
        return ebookUrl;
    }

    public void setEbookUrl(String ebookUrl) {
        this.ebookUrl = ebookUrl;
    }

    @ManyToMany(mappedBy = "toRead")
    public List<User> getToReadUsers() {
        return toReadUsers;
    }

    public void setToReadUsers(List<User> toReadUsers) {
        this.toReadUsers = toReadUsers;
    }

    @ManyToMany(mappedBy = "reading")
    public List<User> getReadingUsers() {
        return readingUsers;
    }

    public void setReadingUsers(List<User> readingUsers) {
        this.readingUsers = readingUsers;
    }

    @ManyToMany(mappedBy = "haveRead")
    public List<User> getHaveReadUsers() {
        return haveReadUsers;
    }

    public void setHaveReadUsers(List<User> haveReadUsers) {
        this.haveReadUsers = haveReadUsers;
    }

    @OneToMany(mappedBy = "book")
    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    @OneToMany(mappedBy = "book")
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
