package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Map<Integer, Item>> items = new HashMap<>();
    private int maxId;

    @Override
    public Item create(Item item) {
        item.setId(++maxId);
        Map<Integer, Item> itemList = new HashMap<>();
        itemList.put(item.getId(), item);
        items.put(item.getOwner().getId(), itemList);

        return item;
    }

    @Override
    public Map<Integer, Item> searchByOwnerId(int ownerId) {

       return items.getOrDefault(ownerId, new HashMap<>());
    }

    @Override
    public Optional<Item> findByOwnerIdAndItemId(int ownerId,  int itemId) {
        return Optional.ofNullable(items.getOrDefault(ownerId, new HashMap<>()).get(itemId));
    }

    @Override
    public Item update(Item item) {
        int ownerId = item.getOwner().getId();
        items.get(ownerId).put(item.getId(), item.toBuilder().build()); //protect
        return item;
    }
}
