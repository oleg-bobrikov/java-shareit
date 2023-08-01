package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;


public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto pathItem(ItemDto itemDto);
}
