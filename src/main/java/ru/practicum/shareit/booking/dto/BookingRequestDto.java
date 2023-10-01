package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validator.TimeStamp;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    @TimeStamp
    private String start;

    @TimeStamp
    private String end;

    @NotNull
    private Long itemId;
}
