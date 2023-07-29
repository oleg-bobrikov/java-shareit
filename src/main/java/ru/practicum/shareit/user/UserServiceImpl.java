package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.hasEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("email " + userDto.getEmail() + " has already assigned.");
        }

        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUser(UserDto userDto) {

        User user = userRepository.findUserById(userDto.getId()).orElseThrow(
                () -> new NotFoundException("user with id " + userDto.getId() + " is not exist"));

        boolean hasChanged = false;

        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
            hasChanged = true;
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepository.hasEmail(userDto.getEmail())) {
                throw new DuplicateEmailException(" user with email " + userDto.getEmail() + " has already assigned.");
            }

            user.setEmail(userDto.getEmail());
            hasChanged = true;
        }

        return UserMapper.toUserDto(hasChanged ? userRepository.update(user) : user);
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findUserById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Integer userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
