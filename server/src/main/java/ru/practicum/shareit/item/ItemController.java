package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static ru.practicum.shareit.common.Constant.USER_ID_HEADER;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemAnswerDto createItem(@RequestHeader(USER_ID_HEADER) long ownerId,
                                    @RequestBody ItemRequestDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentAnswerDto createComment(@RequestHeader(USER_ID_HEADER) long authorId,
                                          @RequestBody CommentRequestDto commentDto,
                                          @PathVariable long itemId) {
        return itemService.createComment(authorId, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemAnswerDto updateItem(@RequestHeader(USER_ID_HEADER) long ownerId,
                                    @RequestBody ItemRequestDto requestDto,
                                    @PathVariable long itemId) {
        return itemService.updateItem(ownerId, requestDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemAnswerDto getItem(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId) {

        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemAnswerDto> getItems(@RequestHeader(USER_ID_HEADER) long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemAnswerDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
