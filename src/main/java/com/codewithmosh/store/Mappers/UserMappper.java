package com.codewithmosh.store.Mappers;

import org.mapstruct.Mapper;

import com.codewithmosh.store.Dtos.RegisterUserRequest;
import com.codewithmosh.store.Dtos.UserDto;
import com.codewithmosh.store.entities.User;

@Mapper(componentModel = "spring")
public interface UserMappper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
}
