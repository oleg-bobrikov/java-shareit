package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

public interface BookingService {
    BookingAnswerDto createBooking(BookingRequestDto bookingRequestDto);

    BookingAnswerDto approve(BookingApproveDto bookingApproveDto);

    BookingAnswerDto getBooking(BookingGetDto bookingGetDto);

    List<BookingAnswerDto> getBookingsByBookerId(Long bookerId, State state);

    List<BookingAnswerDto> getBookingsByOwnerId(Long ownerId, State state);
}
