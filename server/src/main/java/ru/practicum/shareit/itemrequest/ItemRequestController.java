package ru.practicum.shareit.itemrequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;


import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestAnswerDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestRequestDto);
    }

    @Operation(summary = "Cписок своих запросов вместе с данными об ответах на них.")
    @GetMapping
    public List<ItemRequestAnswerDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.getRequestsByOwner(requesterId);
    }


    @Operation(summary = "Данные об одном конкретном запросе вместе с данными об ответах на него " +
            "в том же формате, что и в эндпоинте GET /requests. " +
            "Посмотреть данные об отдельном запросе может любой пользователь.")
    @GetMapping({"/{requestId}"})
    public ItemRequestAnswerDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Parameter(description = "ID запроса для поиска")
                                        @PathVariable long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

    @Operation(summary = "Cписок запросов, созданных другими пользователями")
    @GetMapping(path = "/all")
    public List<ItemRequestAnswerDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam int from,
                                             @RequestParam int size) {
        return itemRequestService.getAllByOthers(userId, from, size);
    }
}
