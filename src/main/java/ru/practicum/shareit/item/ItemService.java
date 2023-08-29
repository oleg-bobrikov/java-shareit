package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto patchItem(ItemDto itemDto);

    ItemDto getItem(ItemDto itemDto);

    List<ItemDto> getItems(Long ownerId);

    List<ItemDto> searchItems(String text);
}
