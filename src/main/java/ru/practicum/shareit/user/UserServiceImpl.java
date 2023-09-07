package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
            return userMapper.toDto(userRepository.save(userMapper.toModel(userDto)));
    }

    @Override
    @Transactional
    public UserDto patchUser(UserDto userDto) {

        User user = userMapper.toModel(getUserById(userDto.getId()));

        boolean hasChanged = false;

        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
            hasChanged = true;
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (!userRepository.findAllByEmail(userDto.getEmail()).isEmpty()) {
                throw new DataIntegrityViolationException(" user with email " + userDto.getEmail() + " has already assigned.");
            }

            user.setEmail(userDto.getEmail());
            hasChanged = true;
        }

        return userMapper.toDto(hasChanged ? userRepository.save(user) : user);
    }


    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

}
