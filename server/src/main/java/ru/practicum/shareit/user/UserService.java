package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

@Service
public interface UserService {
    UserDto createUser(UserRequestDto userDto);

    UserDto patchUser(long userId, UserRequestDto userDto);

    UserDto getUserById(long userId);

    void deleteUserById(long userId);

    List<UserDto> getUsers();
}
