package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingGetDto {
    private Long userId;
    private Long bookingId;
}