package com.mikolajczyk.book.backend.manager.domain;

import com.mikolajczyk.book.backend.manager.log.domain.Log;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class User {

    private long id;
    private String googleId;
    private String name;
    private String lastname;
    private String email;
    private String locale;
    private LocalDate created;
    private String pictureUrl;
    private List<Book> toRead;
    private List<Book> reading;
    private List<Book> haveRead;
    private List<Log> logs;

    public User(String googleId, String name, String lastname, String email, String locale, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.locale = locale;
        this.pictureUrl = pictureUrl;
    }

    public User(String googleId, String name, String lastname, String email, String locale, LocalDate created, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.locale = locale;
        this.created = created;
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

    @Column(name = "LOCATE")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Column(name = "CREATED")
    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }


    @Column(name = "PICTURE_URL")
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String picture) {
        this.pictureUrl = picture;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
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

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
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

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
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

    @OneToMany(targetEntity = Log.class, mappedBy = "user")
    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }
}
