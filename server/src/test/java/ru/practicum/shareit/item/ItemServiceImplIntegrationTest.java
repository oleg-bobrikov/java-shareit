package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Generator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-h2.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    ItemRepository itemRepository;

    @Test
    @DirtiesContext
    void createItem_createEntityOnService_returnSameEntityFromRepository() {
        // Arrange
        UserRequestDto userShortDto = Generator.makeUserRequestDto1();
        UserDto newUser = userService.createUser(userShortDto);
        ItemRequestDto itemPostRequestDto = Generator.makeItemRequestDtoWithoutRequestId();

        // Act
        ItemAnswerDto expected = itemService.createItem(newUser.getId(), itemPostRequestDto);
        final long itemId = expected.getId();
        ItemAnswerDto actual = itemService.getItem(expected.getId(), newUser.getId());
        Optional<Item> createdItem = itemRepository.findById(itemId);

        // Assert
        assertThat(actual).isEqualTo(expected);

        assertThat(createdItem).isPresent()
                .satisfies(item -> assertThat(item.get())
                        .hasFieldOrPropertyWithValue("id", itemId)
                        .hasFieldOrPropertyWithValue("name", expected.getName())
                        .hasFieldOrPropertyWithValue("description", expected.getDescription())
                        .hasFieldOrPropertyWithValue("isAvailable", expected.getAvailable()));
    }
}
