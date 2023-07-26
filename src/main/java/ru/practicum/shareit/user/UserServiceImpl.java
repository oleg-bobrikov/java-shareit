package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null){
            throw new DuplicateEmailException("email " + userDto.getEmail() + " has already assigned.");
        }

        User user =  userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }
}
