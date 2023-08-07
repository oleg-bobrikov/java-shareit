package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto pathItem(ItemDto itemDto);

    ItemDto getItem(ItemDto itemDto);

    List<ItemDto> getItems(int ownerId);

    List<ItemDto> searchItems(String text);
}
