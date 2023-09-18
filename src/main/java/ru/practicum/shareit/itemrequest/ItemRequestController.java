package ru.practicum.shareit.itemrequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemAnswerDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @Operation(summary = "Cписок своих запросов вместе с данными об ответах на них.")
    @GetMapping
    public List<ItemAnswerDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.getRequestsByOwner(requesterId);
    }


    @Operation(summary = "Данные об одном конкретном запросе вместе с данными об ответах на него " +
            "в том же формате, что и в эндпоинте GET /requests. " +
            "Посмотреть данные об отдельном запросе может любой пользователь.")
    @GetMapping({"/{requestId}"})
    public ItemAnswerDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Parameter(description = "ID запроса для поиска")
                             @PathVariable @Min(1) long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

    @Operation(summary = "Cписок запросов, созданных другими пользователями")
    @GetMapping(path = "/all")
    public List<ItemAnswerDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "20") @Min(1) int size) {

        return itemRequestService.getAllByOthers(userId, from, size);
    }
}
