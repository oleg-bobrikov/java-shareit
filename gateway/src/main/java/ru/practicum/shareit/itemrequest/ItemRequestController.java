package ru.practicum.shareit.itemrequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.validator.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestRequestDto);
    }

    @Operation(summary = "Cписок своих запросов вместе с данными об ответах на них.")
    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestClient.getRequestsByOwner(requesterId);
    }

    @Operation(summary = "Данные об одном конкретном запросе вместе с данными об ответах на него " +
            "в том же формате, что и в эндпоинте GET /requests. " +
            "Посмотреть данные об отдельном запросе может любой пользователь.")
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Parameter(description = "ID запроса для поиска")
                                          @PathVariable @Positive long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }

    @Operation(summary = "Cписок запросов, созданных другими пользователями")
    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "32") @Positive int size) {

        return itemRequestClient.getAllByOthers(userId, from, size);
    }

}
