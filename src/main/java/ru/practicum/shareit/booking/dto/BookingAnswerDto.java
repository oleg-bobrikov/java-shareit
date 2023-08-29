package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
@Data
@Builder
public class BookingAnswerDto {
    private Long id;
    private String start;
    private String end;
    private Item item;
    private Status status;
    private User booker;
}
