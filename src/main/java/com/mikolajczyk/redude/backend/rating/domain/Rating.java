package com.mikolajczyk.redude.backend.rating.domain;

import com.mikolajczyk.redude.backend.domain.Book;
import com.mikolajczyk.redude.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RATINGS")
@NoArgsConstructor
public class Rating {

    private long id;
    private User user;
    private Book book;
    private int value;
    private String comment;

    public Rating(User user, Book book, int value, String comment) {
        this.user = user;
        this.book = book;
        this.value = value;
        this.comment = comment;
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

    @ManyToOne(
            targetEntity = User.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
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
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
            )
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Column(name = "VALUES")
    public int getValue() {
        return value;
    }

    public void setValue(int quantity) {
        this.value = quantity;
    }

    @Column(name = "COMMENTS")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
