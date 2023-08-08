package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(imports = User.class, componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserDto userDto);
}

