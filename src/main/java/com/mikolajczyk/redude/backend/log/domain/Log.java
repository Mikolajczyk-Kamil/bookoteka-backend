package com.mikolajczyk.redude.backend.log.domain;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.log.type.LogType;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOGS")
@NoArgsConstructor
public class Log {

    private long id;
    private LogType type;
    private LocalDateTime dateTime;
    private User user;
    private Book book;

    public Log(LogType type, User user, Book book) {
        this.type = type;
        this.dateTime = LocalDateTime.now();
        this.user = user;
        this.book = book;
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
    @Column(name = "TYPE")
    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    @NotNull
    @Column(name = "DATE")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @ManyToOne(
            targetEntity = User.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(
            targetEntity = Book.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
