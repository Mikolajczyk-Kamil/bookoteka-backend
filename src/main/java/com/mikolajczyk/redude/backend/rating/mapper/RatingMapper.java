package com.mikolajczyk.redude.backend.rating.mapper;

import com.mikolajczyk.redude.backend.mapper.BookMapper;
import com.mikolajczyk.redude.backend.mapper.UserMapper;
import com.mikolajczyk.redude.backend.rating.domain.Rating;
import com.mikolajczyk.redude.backend.rating.dto.RatingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RatingMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public RatingDto mapToRatingDto(Rating rating) {
        return new RatingDto(
                userMapper.mapToUserDto(rating.getUser()),
                rating.getValue(),
                rating.getComment()
        );
    }

    public Rating mapToRating(RatingDto ratingDto) {
        return new Rating(
                userMapper.mapToUser(ratingDto.getUserDto()),
                bookMapper.mapToBook(ratingDto.getBookDto()),
                ratingDto.getValue(),
                ratingDto.getComment()
        );
    }

    public List<RatingDto> mapToListRatingDto(List<Rating> ratings) {
        return ratings.stream().map(this::mapToRatingDto).collect(Collectors.toList());
    }
}
