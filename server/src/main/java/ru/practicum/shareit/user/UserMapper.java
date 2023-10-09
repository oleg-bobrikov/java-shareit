package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserHeaderDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

@Mapper(imports = {User.class, UserRequestDto.class}, componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    UserHeaderDto toHeaderDto(User user);

    @Mapping(target = "id", expression = "java(null)")
    UserDto toDto(UserRequestDto userDto);

    List<UserDto> toDtoList(List<User> users);

    User toModel(UserDto userDto);

    @Mapping(target = "id", expression = "java(null)")
    User toModel(UserRequestDto userDto);


}

