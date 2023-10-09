package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemHeaderDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {Item.class, User.class, ItemHeaderDto.class}, componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "item", expression = "java(ItemHeaderDto.builder()" +
            "                .id(booking.getItem().getId())" +
            "                .name(booking.getItem().getName())" +
            "                .build())")
    BookingAnswerDto toDto(Booking booking);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    BookingShortDto toShortDto(Booking booking);

    List<BookingAnswerDto> toDtoList(List<Booking> bookings);
}
