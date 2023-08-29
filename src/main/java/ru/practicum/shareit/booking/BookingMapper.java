package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {Item.class, User.class, InstantConverter.class}, componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "start", expression = "java(InstantConverter.toPattern(booking.getStart()))")
    @Mapping(target = "end", expression = "java(InstantConverter.toPattern(booking.getEnd()))")
    BookingAnswerDto toDto(Booking booking);

    List<BookingAnswerDto> toDtoList(List<Booking> bookings);
}
