package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.AnswerDto;
import ru.practicum.shareit.itemrequest.dto.RequestDto;

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
    public AnswerDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Valid @RequestBody RequestDto requestDto) {
        return itemRequestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<AnswerDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.getRequestsByOwner(requesterId);
    }

    @GetMapping({"/{id}"})
    public AnswerDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable @Min(1) long id) {
        return itemRequestService.getById(id, userId);
    }

    @GetMapping(path = "/all")
    public List<AnswerDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                  @RequestParam(defaultValue = "20") @Min(1) int size) {

        return itemRequestService.getAllByOthers(userId, from, size);
    }
}
