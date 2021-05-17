package com.mikolajczyk.redude.backend.rating.dto;


import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
@Setter
public class RatingDto {

    private long id;
    private UserDto userDto;
    private BookDto bookDto;
    private int value;
    private String comment;

    public RatingDto(UserDto userDto, int value, String comment) {
        this.userDto = userDto;
        this.value = value;
        this.comment = comment;
    }

    public RatingDto(UserDto userDto, BookDto bookDto, int value, String comment) {
        this.userDto = userDto;
        this.bookDto = bookDto;
        this.value = value;
        this.comment = comment;
    }
}
