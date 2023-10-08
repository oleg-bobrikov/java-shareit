package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.validator.OnCreate;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Creating item {}", requestDto);
        return itemClient.createItem(ownerId, requestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                                @Valid @RequestBody CommentRequestDto requestDto,
                                                @PathVariable long itemId) {
        log.info("Creating comment {} by author_id={} for item_id={}", requestDto, authorId, itemId);
        return itemClient.createComment(authorId, requestDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody ItemRequestDto requestDto,
                                             @PathVariable long itemId) {
        log.info("Updating item_id {} by {} and user_id {}", itemId, requestDto, ownerId);
        return itemClient.updateItem(ownerId, requestDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Getting item by user_id {} and item_id {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting items by owner_id {}", ownerId);
        return itemClient.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text) {
        return itemClient.searchItems(userId, text);
    }
}
