package com.mikolajczyk.book.backend.manager.mapper;

import com.mikolajczyk.book.backend.manager.domain.User;
import com.mikolajczyk.book.backend.manager.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getGoogleId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getLocale(),
                user.getCreated(),
                user.getPictureUrl());
    }

    public User mapToUser(UserDto userDto) {
        return new User(
                userDto.getGoogleId(),
                userDto.getName(),
                userDto.getLastname(),
                userDto.getEmail(),
                userDto.getLocale(),
                userDto.getCreated(),
                userDto.getPictureUrl()
        );
    }
}
