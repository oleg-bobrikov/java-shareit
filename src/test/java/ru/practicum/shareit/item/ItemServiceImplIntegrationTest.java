package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemServiceImplIntegrationTest {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;

    @Test
    @DirtiesContext
    void createItem() {
        // Arrange
        UserShortDto userShortDto = Generator.makeUserShortDto();
        UserDto newUser = userService.createUser(userShortDto);
        ItemPostRequestDto itemPostRequestDto = Generator.makeItemPostRequestDtoWithoutRequestId();

        // Act
        ItemAnswerDto expected = itemService.createItem(newUser.getId(), itemPostRequestDto);
        ItemAnswerDto actual = itemService.getItem(expected.getId(), newUser.getId());

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
