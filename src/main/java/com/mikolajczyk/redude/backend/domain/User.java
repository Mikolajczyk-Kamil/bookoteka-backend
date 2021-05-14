package com.mikolajczyk.redude.backend.domain;

import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User {

    private long id;
    private String googleId;
    private String name;
    private String lastname;
    private String email;
    private String pictureUrl;
    private List<Book> toRead = new ArrayList<>();
    private List<Book> reading = new ArrayList<>();
    private List<Book> haveRead = new ArrayList<>();
    private List<Log> logs = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    public User(String googleId, String name, String lastname, String email, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public User(long id, String googleId, String name, String lastname, String email, String pictureUrl) {
        this.id = id;
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public User(long id, String name, String lastname, String email, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public void addBookToRead(Book book){
        this.toRead.add(book);
        book.getToReadUsers().add(this);
        removeBookReading(book);
        removeBookHaveRead(book);
    }

    public void removeBookToRead(Book book){
        this.toRead.remove(book);
        book.getToReadUsers().remove(this);
    }

    public void addBookReading(Book book){
        this.reading.add(book);
        book.getReadingUsers().add(this);
        removeBookToRead(book);
        removeBookHaveRead(book);
    }

    public void removeBookReading(Book book){
        this.reading.remove(book);
        book.getReadingUsers().remove(this);
    }

    public void addBookHaveRead(Book book){
        this.haveRead.add(book);
        book.getHaveReadUsers().add(this);
        removeBookToRead(book);
        removeBookReading(book);
    }

    public void removeBookHaveRead(Book book){
        this.haveRead.remove(book);
        book.getHaveReadUsers().remove(this);
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
    @Column(name = "GOOGLE_ID")
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String gId) {
        this.googleId = gId;
    }

    @NotNull
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Column(name = "LASTNAME")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @NotNull
    @Column(name = "EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PICTURE_URL")
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String picture) {
        this.pictureUrl = picture;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "USERS_BOOKS_TO_READ",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BOOK_ID")
    )
    public List<Book> getToRead() {
        return toRead;
    }

    public void setToRead(List<Book> toRead) {
        this.toRead = toRead;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "USERS_BOOKS_READING",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BOOK_ID")
    )
    public List<Book> getReading() {
        return reading;
    }

    public void setReading(List<Book> reading) {
        this.reading = reading;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "USERS_BOOKS_HAVE_READ",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BOOK_ID")
    )
    public List<Book> getHaveRead() {
        return haveRead;
    }

    public void setHaveRead(List<Book> haveRead) {
        this.haveRead = haveRead;
    }

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
