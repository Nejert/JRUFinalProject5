package com.javarush.jrufinalproject5.dto;

import com.javarush.jrufinalproject5.dto.user.UserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDto {
    @Mapping(target = "tasks", ignore = true)
    User from(UserIn userIn);

    UserOut from(User user);
}

