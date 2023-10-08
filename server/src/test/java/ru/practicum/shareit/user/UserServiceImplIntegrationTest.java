package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Generator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-h2.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    void createUser_createEntityOnService_returnSameEntityFromRepository() {
        // Arrange
        UserRequestDto userShortDto = Generator.makeUserRequestDto1();

        // Act
        UserDto actual = userService.createUser(userShortDto);
        final long userId = actual.getId();
        UserDto expected = userService.getUserById(actual.getId());

        Optional<User> createdUser = userRepository.findById(userId);

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", userShortDto.getName())
                .hasFieldOrPropertyWithValue("email", userShortDto.getEmail());

        assertThat(actual).isEqualTo(expected);


        assertThat(createdUser).isPresent()
                .satisfies(user -> assertThat(user.get())
                        .hasFieldOrPropertyWithValue("id", userId)
                        .hasFieldOrPropertyWithValue("name", expected.getName())
                        .hasFieldOrPropertyWithValue("email", expected.getEmail()));
    }
}