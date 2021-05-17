package com.mikolajczyk.redude.backend.mapper;

import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTestSuite {

    @Autowired
    private UserMapper mapper;

    @Test
    public void testMapToUserDto() {
        //Given
        User user = new User(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");

        //When
        UserDto result = mapper.mapToUserDto(user);

        //Then
        assertEquals(1L, result.getId());
        assertNull(result.getGoogleId());
        assertEquals("name1", result.getName());
        assertEquals("lastname1", result.getLastname());
        assertEquals("email1", result.getEmail());
        assertEquals("pictureUrl1", result.getPictureUrl());
    }

    @Test
    public void testMapToUser() {
        //Given
        UserDto userDto = new UserDto(1L, "googleId1", "name1", "lastname1", "email1", "pictureUrl1");

        //When
        User result = mapper.mapToUser(userDto);

        //Then
        assertEquals(1L, result.getId());
        assertEquals("googleId1", result.getGoogleId());
        assertEquals("name1", result.getName());
        assertEquals("lastname1", result.getLastname());
        assertEquals("email1", result.getEmail());
        assertEquals("pictureUrl1", result.getPictureUrl());
    }
}
