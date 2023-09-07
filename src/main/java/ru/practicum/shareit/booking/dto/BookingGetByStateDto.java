package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.State;

@Data
@Builder
public class BookingGetByStateDto {
   private Long bookerId;
    private State state;
}
