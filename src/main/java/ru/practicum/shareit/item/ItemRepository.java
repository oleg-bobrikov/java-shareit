package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;
import java.util.Optional;


public interface ItemRepository {
    Item create(Item item);

    Map<Integer, Item> searchByOwnerId(int ownerId);
    Optional<Item> findByOwnerIdAndItemId(int ownerId,  int itemId);

    Item update(Item item);
}
