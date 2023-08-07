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
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.hasEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("email " + userDto.getEmail() + " has already assigned.");
        }

        User user = userRepository.create(userMapper.toModel(userDto));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto patchUser(UserDto userDto) {

        User user = userRepository.findById(userDto.getId()).orElseThrow(
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

        return userMapper.toDto(hasChanged ? userRepository.update(user) : user);
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

}
