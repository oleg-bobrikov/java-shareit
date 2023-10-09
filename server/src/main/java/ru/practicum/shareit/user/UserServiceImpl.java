package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserRequestDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toModel(userDto)));
    }

    @Override
    @Transactional
    public UserDto patchUser(long userId, UserRequestDto userDto) {

        User user = userMapper.toModel(getUserById(userId));
        boolean hasChanged = false;

        if (userDto.getName() == null && userDto.getEmail() == null) {
            return userMapper.toDto(user);
        }

        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
            hasChanged = true;
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            validateEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
            hasChanged = true;
        }

        return userMapper.toDto(hasChanged ? userRepository.save(user) : user);
    }

    private void validateEmail(String email) {
        if (!userRepository.findAllByEmail(email).isEmpty()) {
            throw new DataIntegrityViolationException(" user with email " + email + " has already assigned.");
        }
    }

    @Override
    @Transactional
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        getUserById(id);

        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

}
