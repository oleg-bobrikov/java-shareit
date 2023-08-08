package ru.practicum.shareit.item;


import java.util.List;

import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    List<Item> searchByOwnerId(int ownerId);

    Optional<Item> searchByOwnerIdAndItemId(int ownerId, int itemId);

    Optional<Item> searchByItemId(int itemId);

    Item update(Item item);

    List<Item> searchByText(String text);
}
