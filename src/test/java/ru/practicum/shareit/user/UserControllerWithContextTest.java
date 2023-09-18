package ru.practicum.shareit.user;

import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserShortDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerWithContextTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    UserShortDto createTestUserShortDtoWithNotValidEmail() {
        return UserShortDto.builder()
                .email("123")
                .name("username")
                .build();
    }

    @Test
    void shouldNotCreateUserWhenEmailIsNotValid() throws Exception {
        UserShortDto request = createTestUserShortDtoWithNotValidEmail();

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
                .andExpect(status().isBadRequest());

    }

}