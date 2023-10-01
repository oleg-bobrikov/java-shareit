package ru.practicum.shareit.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerWithoutContextTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private UserController controller;
    @Mock
    private UserService userService;
    private MockMvc mvc;

    UserShortDto createTestUserShortDtoWithNotValidEmail() {
        return UserShortDto.builder()
                .email("123")
                .name("username")
                .build();
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getUsers_userExist_returnListOfUsersWithStatusOK() throws Exception {
        UserDto userDto = Generator.makeUserDto();

        when(userService.getUsers())
                .thenReturn(Collections.singletonList(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUserById_userExist_returnStatusOkAndValidJSON() throws Exception {
        UserDto userDto = Generator.makeUserDto();
        long userId = 1L;
        when(userService.getUserById(userId))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void createUser_validUserDto_returnsStatusOkAndValidJson() throws Exception {
        UserDto userDto = Generator.makeUserDto();

        UserShortDto request = UserShortDto.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();

        when(userService.createUser(request))
                .thenAnswer(invocationOnMock -> {
                    UserDto answer = userMapper.toDto(invocationOnMock.getArgument(0, UserShortDto.class));
                    answer.setId(1L);
                    return answer;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1)).createUser(request);
    }

    @Test
    void delete_validUserId_returnNoContent() throws Exception {

        long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void patchUser_userFound_returnsStatusOkAndValidJson() throws Exception {

        UserShortDto patch = UserShortDto.builder()
                .name("new name")
                .email("updateEamil@mail.com")
                .build();
        long userId = 1L;

        when(userService.patchUser(userId, patch))
                .thenAnswer(invocationOnMock -> {
                    UserDto answer = userMapper.toDto(invocationOnMock.getArgument(1, UserShortDto.class));
                    answer.setId(userId);
                    return answer;
                });

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(patch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.email", is(patch.getEmail())))
                .andExpect(jsonPath("$.name", is(patch.getName())));

        verify(userService, times(1)).patchUser(userId, patch);
    }

    @Test
    void createUser_invalidEmail_returnsBadRequest() throws Exception {
        UserShortDto request = createTestUserShortDtoWithNotValidEmail();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assert exception != null;
                    assert exception instanceof MethodArgumentNotValidException;
                });
        verify(userService, never()).createUser(request);
    }

}