package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.Generator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void search_itemExist_returnItemList() {
        // Arrange
        User user1 = Generator.makeUser1();
        User owner = userRepository.save(user1);
        Item item = Item.builder()
                .isAvailable(true)
                .description("item description")
                .name("item name")
                .owner(owner)
                .build();
        Item savedItem = itemRepository.save(item);

        // Act
        List<Item> actual = itemRepository.search("description");

        // Assert
        assertThat(actual).asList().hasSize(1)
                .satisfies(list -> assertEquals(list.get(0), savedItem));
    }
}