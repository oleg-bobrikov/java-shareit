package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void createUser_userIsValid_saveUserAndReturnDto() {
        UserShortDto userShortDto = Generator.makeUserShortDto1();

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user1 = invocationOnMock.getArgument(0, User.class);
                    user1.setId(1L);
                    return user1;
                });

        assertThat(userService.createUser(userShortDto))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", userShortDto.getName())
                .hasFieldOrPropertyWithValue("email", userShortDto.getEmail());

        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void getUserById_userExist_returnDto() {
        User user = Generator.makeUser1();
        when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));

        assertThat(userService.getUserById(1))
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());

        verify(userRepository, times(1)).findById(Mockito.any(Long.class));
    }

    @Test
    void getUserById_UserIsNotExist_throwNotFoundException() {

        when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(100))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, times(1)).findById(Mockito.any(Long.class));
    }

    @Test
    void deleteUserById_userExists_delete() {
        User user = Generator.makeUser1();
        when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));

        userService.deleteUserById(user.getId());
        verify(userRepository, times(1)).findById(Mockito.any(Long.class));
        verify(userRepository, times(1)).deleteById(Mockito.any(Long.class));
    }

    @Test
    void getUsers_usersAreExist_returnListOfDto() {
        User user = Generator.makeUser1();
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList((user)));

        assertThat(userService.getUsers())
                .asList()
                .hasSize(1)
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", user.getId())
                        .hasFieldOrPropertyWithValue("name", user.getName())
                        .hasFieldOrPropertyWithValue("email", user.getEmail()));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void patchUser_noChanges_skip() {

        // arrange
        UserShortDto userCreateRequest1 = Generator.makeUserShortDto1();
        Long userId = 1L;
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User answer = invocationOnMock.getArgument(0, User.class);
                    answer.setId(userId);
                    return answer;
                });
        when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User answer = userMapper.toModel(userCreateRequest1);
                    answer.setId(userId);
                    return Optional.of(answer);
                });
        // act
        UserDto createdUser = userService.createUser(userCreateRequest1);

        // assert
        UserDto actual = userService.patchUser(createdUser.getId(), UserShortDto.builder().build());
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("name", createdUser.getName())
                .hasFieldOrPropertyWithValue("email", createdUser.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void patchUser_emailChanged_updateEmail() {
        // arrange
        UserShortDto userCreateRequest1 = Generator.makeUserShortDto1();
        Long userId = 1L;
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User answer = invocationOnMock.getArgument(0, User.class);
                    answer.setId(userId);
                    return answer;
                });
        when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User answer = userMapper.toModel(userCreateRequest1);
                    answer.setId(userId);
                    return Optional.of(answer);
                });
        String updatedEmail = "updated_" + userCreateRequest1.getEmail();
        UserDto createdUser = userService.createUser(userCreateRequest1);
        UserShortDto patchUser = UserShortDto.builder()
                .email(updatedEmail)
                .build();

        // act
        UserDto actual = userService.patchUser(createdUser.getId(), patchUser);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("name", createdUser.getName())
                .hasFieldOrPropertyWithValue("email", patchUser.getEmail());
        verify(userRepository, times(2)).save(Mockito.any(User.class));
    }

    @Test
    void patchUser_nameChanged_updateName() {
        // arrange
        UserShortDto userCreateRequest1 = Generator.makeUserShortDto1();
        Long userId = 1L;
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User answer = invocationOnMock.getArgument(0, User.class);
                    answer.setId(userId);
                    return answer;
                });
        when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User answer = userMapper.toModel(userCreateRequest1);
                    answer.setId(userId);
                    return Optional.of(answer);
                });
        String updateName = "updated_" + userCreateRequest1.getName();
        UserDto createdUser = userService.createUser(userCreateRequest1);
        UserShortDto patchUser = UserShortDto.builder()
                .name(updateName)
                .build();

        // act
        UserDto actual = userService.patchUser(createdUser.getId(), patchUser);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("name", patchUser.getName())
                .hasFieldOrPropertyWithValue("email", createdUser.getEmail());
        verify(userRepository, times(2)).save(Mockito.any(User.class));
    }

    @Test
    void patchUser_sameName_skipUpdate() {
        // arrange
        UserShortDto request = Generator.makeUserShortDto1();
        final Long userId = 1L;
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User answer = invocationOnMock.getArgument(0, User.class);
                    answer.setId(userId);
                    return answer;
                });
        when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User answer = userMapper.toModel(request);
                    answer.setId(userId);
                    return Optional.of(answer);
                });
        String updateName = request.getName();
        UserDto createdUser = userService.createUser(request);
        UserShortDto patchUser = UserShortDto.builder()
                .name(updateName)
                .build();

        // act
        UserDto actual = userService.patchUser(userId, patchUser);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("name", patchUser.getName())
                .hasFieldOrPropertyWithValue("email", createdUser.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void patchUser_sameEmail_skipUpdate() {
        // arrange
        UserShortDto request = Generator.makeUserShortDto1();
        final Long userId = 1L;
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User answer = invocationOnMock.getArgument(0, User.class);
                    answer.setId(userId);
                    return answer;
                });
        when(userRepository.findById(userId))
                .thenAnswer(invocationOnMock -> {
                    User answer = userMapper.toModel(request);
                    answer.setId(userId);
                    return Optional.of(answer);
                });
        UserDto createdUser = userService.createUser(request);
        UserShortDto patchUser = UserShortDto.builder()
                .email(request.getEmail())
                .build();

        // act
        UserDto actual = userService.patchUser(userId, patchUser);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("name", createdUser.getName())
                .hasFieldOrPropertyWithValue("email", patchUser.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void validateEmail_newEmailExist_throwException() {
        // arrange
        UserDto userDto = Generator.makeUserDto();
        User user = Generator.makeUser1();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMapper.toModel(userDto)));
        when(userRepository.findAllByEmail(anyString()))
                .thenReturn(Collections.singletonList(user));

        UserShortDto patchUser = UserShortDto.builder()
                .email("new_email" + userDto.getEmail())
                .build();
        // assert
        assertThrows(DataIntegrityViolationException.class, () -> userService.patchUser(userDto.getId(), patchUser));

    }
}