package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto createUser(UserDto user);

    UserDto patchUser(UserDto userDto);

    UserDto getUserById(Integer id);

    void delete(Integer userId);

    List<UserDto> getUsers();
}
