package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        return itemService.createItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        return itemService.patchItem(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId) {
        ItemDto itemDto = ItemDto.builder().id(itemId).ownerId(ownerId).build();
        return itemService.getItem(itemDto);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
