package com.mikolajczyk.redude.backend.dto;

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
public class UserDto {

    private long id;
    private String googleId;
    private String name;
    private String lastname;
    private String email;
    private String pictureUrl;

    public UserDto(long id, String name, String lastname, String email, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public UserDto(String googleId, String name, String lastname, String email, String pictureUrl) {
        this.googleId = googleId;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }
}
