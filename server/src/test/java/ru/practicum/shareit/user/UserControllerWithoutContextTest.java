package ru.practicum.shareit.user;

import lombok.SneakyThrows;
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
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Generator;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerWithoutContextTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private UserController controller;
    @Mock
    private UserService userService;
    private MockMvc mvc;

    UserRequestDto createTestUserRequestDtoWithNotValidEmail() {
        return new UserRequestDto("username", "not valid email");
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
    @SneakyThrows
    void createUser_validUserDto_returnsStatusOkAndValidJson() {
        // Arrange
        UserDto userDto = Generator.makeUserDto();
        UserRequestDto userRequestDto = new UserRequestDto(userDto.getName(), userDto.getEmail());

        Long userId = 1L;
        UserDto expected = userMapper.toDto(userRequestDto);
        expected.setId(userId);

        when(userService.createUser(any(UserRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto answer = userMapper.toDto(invocationOnMock.getArgument(0, UserRequestDto.class));
                    answer.setId(userId);
                    return answer;
                });

        // Act and assert
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    void delete_validUserId_returnNoContent() throws Exception {

        long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    @SneakyThrows
    void patchUser_userFound_returnsStatusOkAndValidJson() {

        UserRequestDto patch = UserRequestDto.builder()
                .name("new name")
                .email("updateEamil@mail.com")
                .build();
        long userId = 1L;

        when(userService.patchUser(anyLong(), any(UserRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto answer = userMapper.toDto(invocationOnMock.getArgument(1, UserRequestDto.class));
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

        verify(userService, times(1)).patchUser(anyLong(), any(UserRequestDto.class));
    }



}