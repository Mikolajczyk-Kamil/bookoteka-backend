package com.mikolajczyk.redude.backend.rating.dto;


import com.mikolajczyk.redude.backend.dto.BookDto;
import com.mikolajczyk.redude.backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RatingDto {

    private long id;
    private UserDto userDto;
    private BookDto bookDto;
    private int value;
    private String comment;
}
