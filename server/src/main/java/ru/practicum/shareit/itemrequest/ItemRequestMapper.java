package ru.practicum.shareit.itemrequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {User.class, Item.class, ItemShortAnswerDto.class}, componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "created", source = "createdDate")
    @Mapping(target = "items", expression = "java(new ArrayList<ItemShortAnswerDto>())")
    ItemRequestAnswerDto toDto(ItemRequest request);

    List<ItemRequestAnswerDto> toDto(List<ItemRequest> requests);

}