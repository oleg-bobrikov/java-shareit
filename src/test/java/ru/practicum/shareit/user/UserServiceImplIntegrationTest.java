package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    void createUser() {
        // Arrange
        UserShortDto userShortDto = Generator.makeUserShortDto();

        // Act
        UserDto actual = userService.createUser(userShortDto);
        UserDto expected = userService.getUserById(actual.getId());

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", userShortDto.getName())
                .hasFieldOrPropertyWithValue("email", userShortDto.getEmail());

        assertThat(actual).isEqualTo(expected);
    }
}