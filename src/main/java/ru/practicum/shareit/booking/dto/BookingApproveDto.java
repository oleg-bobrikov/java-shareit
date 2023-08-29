package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingApproveDto {
    Long ownerId;
    Long bookingId;
    Boolean approved;
}
