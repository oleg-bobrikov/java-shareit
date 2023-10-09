package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.common.Constant.DATE_TIME_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @Future
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
}