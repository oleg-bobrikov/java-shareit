package ru.practicum.shareit.itemrequest;

import ru.practicum.shareit.itemrequest.dto.ItemAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemAnswerDto createRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemAnswerDto> getAllByOthers(long userId, int from, int size);

    ItemAnswerDto getById(long id, long userId);

    List<ItemAnswerDto> getRequestsByOwner(long userId);
}
