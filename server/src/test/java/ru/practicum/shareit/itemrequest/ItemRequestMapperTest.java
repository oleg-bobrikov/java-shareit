package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.util.Generator;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    private ItemRequestMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(ItemRequestMapper.class);
    }

    @Test
    void toDto_itemRequestIstNull_returnNull() {
        assertNull(mapper.toDto((ItemRequest) null));
    }

    @Test
    void toDto_ItemRequestAnswerDtoListIstNull_returnNull() {
        assertNull(mapper.toDto((List<ItemRequest>) null));
    }

    @Test
    void toDto_itemRequestNotNull_returnDto() {
        // Assert
        ItemRequest itemRequest = Generator.makeItemRequest1();

        // Act
        ItemRequestAnswerDto actual = mapper.toDto(itemRequest);

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("requester", itemRequest.getRequester())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreatedDate());
    }

    @Test
    void toDto_itemRequestList_returnDtoList() {
        // Assert
        ItemRequest itemRequest = Generator.makeItemRequest1();

        // Act
        List<ItemRequestAnswerDto> actual = mapper.toDto(Collections.singletonList(itemRequest));

        // Assert
        assertThat(actual).asList()
                .hasSize(1)
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", itemRequest.getId())
                        .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                        .hasFieldOrPropertyWithValue("requester", itemRequest.getRequester())
                        .hasFieldOrPropertyWithValue("created", itemRequest.getCreatedDate()));
    }
}