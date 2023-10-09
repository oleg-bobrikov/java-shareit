package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemHeaderDto;
import ru.practicum.shareit.user.dto.UserHeaderDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.common.Constant.DATE_TIME_PATTERN;

@Data
@Builder
public class BookingAnswerDto {
    private Long id;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;

    private ItemHeaderDto item;
    private Status status;
    private UserHeaderDto booker;
}
