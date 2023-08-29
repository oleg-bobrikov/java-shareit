package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ru.practicum.shareit.item.ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;


    @Override
    public ItemDto createItem(ItemDto itemDto) {

        User user = userMapper.toModel(userService.getUserById(itemDto.getOwnerId()));
        Item item = itemMapper.toModel(itemDto);
        item.setOwner(user);

        return itemMapper.toDto(itemRepository.save(itemMapper.toModel(itemDto)));
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto) {
        Item item = itemRepository.findByIdAndOwnerId(itemDto.getId(), itemDto.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Item with id " + itemDto.getId() + " is not exist"));

        boolean hasChanged = false;

        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
            hasChanged = true;
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {

            item.setDescription(itemDto.getDescription());
            hasChanged = true;
        }

        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(item.getIsAvailable())) {

            item.setIsAvailable(itemDto.getAvailable());
            hasChanged = true;
        }

        return itemMapper.toDto(hasChanged ? itemRepository.save(item) : item);
    }

    @Override
    public ItemDto getItem(ItemDto itemDto) {
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item with id " + itemDto.getId() + " is not exist"));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItems(Long ownerId) {
        return itemMapper.toDtoList(itemRepository.findByOwnerId(ownerId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toDtoList(itemRepository.search(text));
    }
}
