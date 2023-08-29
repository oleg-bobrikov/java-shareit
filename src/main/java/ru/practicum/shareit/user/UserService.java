package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto createUser(UserDto user);

    UserDto patchUser(UserDto userDto);

    UserDto getUserById(Long id);

    void deleteUserById(Long userId);

    List<UserDto> getUsers();
}
