package com.mikolajczyk.book.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Getter
@Setter
public class UserDto {

    private long id;
    private String googleId;
    private String name;
    private String lastname;
    private String email;
    private String locale;
    private LocalDate created;
    private String pictureUrl;
    private List<BookDto> toRead;
    private List<BookDto> reading;
    private List<BookDto> haveRead;

    public UserDto(String googleId, String name, String lastname, String email, String locale, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.locale = locale;
        this.pictureUrl = pictureUrl;
    }

    public UserDto(String googleId, String name, String lastname, String email, String locale, LocalDate created, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.locale = locale;
        this.created = created;
        this.pictureUrl = pictureUrl;
    }
}
