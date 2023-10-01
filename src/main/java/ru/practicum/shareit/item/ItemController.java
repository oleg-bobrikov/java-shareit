package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemAnswerDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                    @Valid @RequestBody ItemPostRequestDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentAnswerDto createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                          @Valid @RequestBody CommentPostRequestDto commentDto,
                                          @PathVariable long itemId) {
        return itemService.createComment(authorId, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemAnswerDto patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                   @Valid @RequestBody ItemPatchRequestDto itemDto,
                                   @PathVariable long itemId) {
        return itemService.patchItem(ownerId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemAnswerDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId) {

        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemAnswerDto> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemAnswerDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
