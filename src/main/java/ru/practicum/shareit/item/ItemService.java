package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemAnswerDto createItem(ItemPostRequestDto itemPostRequestDto);

    ItemAnswerDto patchItem(ItemPatchRequestDto itemPatchRequestDto);

    ItemAnswerDto getItem(long itemId, long userId);

    List<ItemAnswerDto> getItems(Long ownerId);

    List<ItemAnswerDto> searchItems(String text);

    CommentAnswerDto createComment(CommentPostRequestDto commentDto);
}
