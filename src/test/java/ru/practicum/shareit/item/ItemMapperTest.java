package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Generator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemMapperTest {
    private ItemMapper itemMapper;

    @BeforeEach
    public void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
    }

    @Test
    void toModel_itemPostRequestDtoNotNull_convertToModel() {
        // arrange
        ItemPostRequestDto itemDto = Generator.makeItemPostRequestDtoWithoutRequestId();

        // act
        Item actual = itemMapper.toModel(itemDto);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("isAvailable", itemDto.getAvailable())
                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                .hasFieldOrPropertyWithValue("description", itemDto.getDescription());

    }

    @Test
    void toDto_itemWithRequest_convertToItemAnswerDto() {
        // arrange
        User user = Generator.makeUser1();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        // act
        ItemAnswerDto actual = itemMapper.toDto(item);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", item.getItemRequest().getId());

    }

    @Test
    void toDto_itemWithoutRequest_convertToItemAnswerDto() {
        // arrange
        User user = Generator.makeUser1();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);

        // act
        ItemAnswerDto actual = itemMapper.toDto(item);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", null);
    }

    @Test
    void toDtoList_itemList_returnDtoList() {
        // arrange
        User user = Generator.makeUser1();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);
        List<Item> items = Collections.singletonList(item);

        // act
        List<ItemAnswerDto> actual = itemMapper.toDtoList(items);

        // assert
        assertThat(actual).asList().hasSize(1);
    }

    @Test
    void toShortDto_itemWithoutRequest_returnItemShortAnswerDto() {
        // arrange
        User user = Generator.makeUser1();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);

        // act
       ItemShortAnswerDto actual = itemMapper.toShortDto(item);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", null);
    }
    @Test
    void toShortDto_itemWithRequest_returnItemShortAnswerDto() {
        // arrange
        User user = Generator.makeUser1();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        // act
        ItemShortAnswerDto actual = itemMapper.toShortDto(item);

        // assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", itemRequest.getId());
    }
    @Test
    void toShortDto_itemList_returnItemShortAnswerDtoList() {
        // arrange
        User user = Generator.makeUser1();
        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(user);
        List<Item> items = Collections.singletonList(item);

        // act
        List<ItemShortAnswerDto> actual = itemMapper.toShortDto(items);

        // assert
        assertThat(actual).asList().hasSize(1);
    }

    @Test
    void toDtoList_itemListNull_returnNull() {
        assertNull(itemMapper.toDtoList(null));
    }

    @Test
    void toModel_itemPostRequestDtoNull_convertToNull() {
        assertNull(itemMapper.toModel(null));
    }

    @Test
    void toDto_itemPostRequestDtoNull_convertToNull() {
        assertNull(itemMapper.toDto((ItemPostRequestDto) null));
    }

    @Test
    void toDto_itemNull_convertToNull() {
        assertNull(itemMapper.toDto((Item) null));
    }

    @Test
    void toShortDto_itemNull_returnNull() {
        assertNull(itemMapper.toShortDto((Item) null));
    }

    @Test
    void toShortDto_itemListNull_returnNull() {
        assertNull(itemMapper.toShortDto((List<Item>) null));
    }


}