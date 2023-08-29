package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SqlException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        try {
            User user = userRepository.save(userMapper.toModel(userDto));
            return userMapper.toDto(user);
        } catch (DataIntegrityViolationException exception) {
            String detailedMessage = exception.getRootCause().toString();
            if (detailedMessage.contains("unique_email")) {
                throw new DuplicateEmailException("email " + userDto.getEmail() + " has already assigned.");
            } else {
                throw new SqlException(detailedMessage);
            }

        } catch (RuntimeException exception) {
            throw new SqlException(exception.getMessage());
        }
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
            if (!userRepository.findAllByEmail(userDto.getEmail()).isEmpty()) {
                throw new DuplicateEmailException(" user with email " + userDto.getEmail() + " has already assigned.");
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
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id " + id + " is not exist"));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

}
