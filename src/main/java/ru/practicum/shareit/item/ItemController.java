package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        return itemService.createItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                             @RequestBody ItemDto itemDto,
                             @PathVariable Integer itemId) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        return itemService.pathItem(itemDto);
    }


}
