package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Map<Integer, Item>> itemsByOwnerId = new HashMap<>();
    private final Map<Integer, Item> itemsByItemId = new HashMap<>();
    private final Map<String, Map<Integer, Item>> itemSearchIndexByWord = new HashMap<>();
    private int maxId;

    private static Item copy(Item item) {
        return item.toBuilder().build();
    }

    @Override
    public Item create(Item item) {
        item.setId(++maxId);
        Map<Integer, Item> itemList = new HashMap<>();
        Item copyItem = item.toBuilder().build(); //protect storage from changes
        itemList.put(copyItem.getId(), copyItem);
        itemsByOwnerId.put(copyItem.getOwner().getId(), itemList);
        itemsByItemId.put(copyItem.getId(), copyItem);

        addIndex(copyItem, copyItem.getName());
        addIndex(copyItem, copyItem.getDescription());

        return item;
    }

    @Override
    public List<Item> searchByOwnerId(int ownerId) {
        return itemsByOwnerId.getOrDefault(ownerId, new HashMap<>())
                .values().stream().map(ItemRepositoryImpl::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> searchByOwnerIdAndItemId(int ownerId, int itemId) {
        Item item = itemsByOwnerId.getOrDefault(ownerId, new HashMap<>()).get(itemId);
        if (item == null) {
            return Optional.empty();
        } else {
            return Optional.of(item.toBuilder().build()); //protect storage from changes
        }
    }

    @Override
    public Optional<Item> searchByItemId(int itemId) {
        Item item = itemsByItemId.get(itemId);
        if (item == null) {
            return Optional.empty();
        } else {
            return Optional.of(item.toBuilder().build()); //protect storage from changes
        }
    }

    @Override
    public Item update(Item item) {
        int ownerId = item.getOwner().getId();
        int itemId = item.getId();
        Item oldItem = searchByOwnerIdAndItemId(ownerId, itemId).orElseThrow(
                () -> new NotFoundException("user with id " + itemId + " is not exist"));

        if (!oldItem.getName().equals(item.getName())) {
            //remove old index
            removeIndex(oldItem, oldItem.getName());

            //add new index
            addIndex(item, item.getName());
        }

        if (!oldItem.getDescription().equals(item.getDescription())) {
            //remove old index
            removeIndex(oldItem, oldItem.getDescription());

            //add new index
            addIndex(item, item.getDescription());
        }
        if (!oldItem.getAvailable().equals(item.getAvailable())) {
            if (item.getAvailable()) {
                //add new index
                addIndex(item, item.getDescription());
            } else {
                //remove old index
                removeIndex(oldItem, oldItem.getDescription());
            }
        }

        itemsByItemId.put(item.getId(), item.toBuilder().build()); //protect storage from changes
        Map<Integer, Item> itemsByOwner = itemsByOwnerId.get(item.getOwner().getId());
        itemsByOwner.remove(itemId);
        itemsByOwner.put(itemId, item.toBuilder().build()); //protect storage from changes

        return item;
    }

    private void removeIndex(Item oldItem, String text) {
        if (text == null) {
            return;
        }
        Arrays.stream(text.split(" "))
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .forEach(key -> itemSearchIndexByWord.compute(key, (k, v) -> {
                            if (v != null) {
                                v.remove(oldItem.getId(), oldItem);
                            }
                            return v;
                        }
                ));
    }

    private void addIndex(Item item, String text) {
        if (text == null || !item.getAvailable()) {
            return;
        }
        Arrays.stream(text.split(" "))
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .forEach(key -> itemSearchIndexByWord.compute(key, (k, v) -> {
                    if (v == null) {
                        v = new HashMap<>();
                    }
                    v.put(item.getId(), item);
                    return v;
                }));
    }

    @Override
    public List<Item> searchByText(String text) {
        String searchString = text.toLowerCase().trim();

        if (searchString.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Item> result = new ArrayList<>(itemSearchIndexByWord.getOrDefault(searchString, new HashMap<>()).values());
        if (result.isEmpty()) {
            return itemsByItemId.values().stream()
                    .filter(item -> item.getName()
                            .contains(searchString) || item.getDescription()
                            .contains(searchString))
                    .collect(Collectors.toList());
        }

        return result;
    }
}
