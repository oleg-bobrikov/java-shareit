package ru.practicum.shareit.itemrequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.validator.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Constant.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestRequestDto);
    }

    @Operation(summary = "Cписок своих запросов вместе с данными об ответах на них.")
    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader(USER_ID_HEADER) long requesterId) {
        return itemRequestClient.getRequestsByOwner(requesterId);
    }

    @Operation(summary = "Данные об одном конкретном запросе вместе с данными об ответах на него " +
            "в том же формате, что и в эндпоинте GET /requests. " +
            "Посмотреть данные об отдельном запросе может любой пользователь.")
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @Parameter(description = "ID запроса для поиска")
                                          @PathVariable @Positive long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }

    @Operation(summary = "Cписок запросов, созданных другими пользователями")
    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {

        return itemRequestClient.getAllByOthers(userId, from, size);
    }

}
