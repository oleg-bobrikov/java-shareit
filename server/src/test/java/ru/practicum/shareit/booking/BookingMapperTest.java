package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.util.Generator;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    private BookingMapper bookingMapper;

    @BeforeEach
    public void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    void toDto_bookingIsNull_returnNull() {
        assertNull(bookingMapper.toDto(null));
    }

    @Test
    void toDto_bookingIsNotNull_returnDto() {
        // Arrange
        Booking booking = Generator.makeBooking();

        // Act
        BookingAnswerDto actual = bookingMapper.toDto(booking);

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", booking.getId())
                .hasFieldOrPropertyWithValue("status", booking.getStatus())
                .hasFieldOrPropertyWithValue("item", booking.getItem())
                .hasFieldOrPropertyWithValue("booker", booking.getBooker())
                .hasFieldOrPropertyWithValue("start", booking.getStartDate())
                .hasFieldOrPropertyWithValue("end", booking.getEndDate());
    }

    @Test
    void toShortDto_bookingIsNotNull_returnDto() {
        // Arrange
        Booking booking = Generator.makeBooking();

        // Act
        BookingShortDto actual = bookingMapper.toShortDto(booking);

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", booking.getId())
                .hasFieldOrPropertyWithValue("bookerId", booking.getBooker().getId());


    }

    @Test
    void toDtoList_bookingIsNotNull_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();

        // Act
        List<BookingAnswerDto> actual = bookingMapper.toDtoList(Collections.singletonList(booking));

        // Assert
        assertThat(actual).asList().hasSize(1)
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", booking.getId())
                        .hasFieldOrPropertyWithValue("status", booking.getStatus())
                        .hasFieldOrPropertyWithValue("item", booking.getItem())
                        .hasFieldOrPropertyWithValue("booker", booking.getBooker())
                        .hasFieldOrPropertyWithValue("start", booking.getStartDate())
                        .hasFieldOrPropertyWithValue("end", booking.getEndDate()));
    }

    @Test
    void toDtoList_bookingListIsNull_returnNull() {
        assertNull(bookingMapper.toDtoList(null));
    }

}