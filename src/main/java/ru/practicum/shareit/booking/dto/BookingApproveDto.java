package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingApproveDto {
    private Long ownerId;
    private Long bookingId;
    private Boolean approved;
}
