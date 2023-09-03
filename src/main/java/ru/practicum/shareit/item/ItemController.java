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
    public ItemAnswerDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                        @Valid @RequestBody ItemPostRequestDto itemDto) {
        itemDto.setOwnerId(ownerId);
        return itemService.createItem(itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentAnswerDto createComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                    @Valid @RequestBody CommentPostRequestDto commentDto,
                                          @PathVariable Long itemId) {
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(authorId);

        return itemService.createComment(commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemAnswerDto patchItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                        @Valid @RequestBody ItemPatchRequestDto itemDto,
                                        @PathVariable Long itemId) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        return itemService.patchItem(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemAnswerDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        ItemGetRequestDto itemDto = ItemGetRequestDto.builder()
                .id(itemId)
                .userId(userId)
                .build();
        return itemService.getItem(itemDto);
    }

    @GetMapping
    public List<ItemAnswerDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemAnswerDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
