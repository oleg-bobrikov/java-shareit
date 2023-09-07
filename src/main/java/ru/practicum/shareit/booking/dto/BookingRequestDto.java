package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.TimeStamp;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class BookingRequestDto {
    private Long id;

    @TimeStamp
    private String start;

    @TimeStamp
    private String end;

    @NotNull
    private Long itemId;

    private Long bookerId;

}
