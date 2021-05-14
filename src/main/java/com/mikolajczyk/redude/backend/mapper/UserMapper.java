package com.mikolajczyk.redude.backend.mapper;

import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getPictureUrl());
    }

    public User mapToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getGoogleId(),
                userDto.getName(),
                userDto.getLastname(),
                userDto.getEmail(),
                userDto.getPictureUrl()
        );
    }
}
