package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {User.class, BookingShortDto.class}, componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "owner", expression = "java(null)")
    @Mapping(target = "itemRequest", expression = "java(null)")
    @Mapping(target = "isAvailable", source = "itemDto.available")
    Item toModel(ItemPostRequestDto itemDto);

    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    @Mapping(target = "available", source = "item.isAvailable")
    @Mapping(target = "lastBooking", expression = "java(null)")
    @Mapping(target = "nextBooking", expression = "java(null)")
    ItemAnswerDto toDto(Item item);

    List<ItemAnswerDto> toDtoList(List<Item> items);
}
