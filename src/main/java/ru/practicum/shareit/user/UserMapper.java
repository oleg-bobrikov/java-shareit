package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Mapper(imports = User.class, componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> users);

    User toModel(UserDto userDto);
}

