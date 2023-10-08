package ru.practicum.shareit.itemrequest;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {User.class, Item.class, ItemShortAnswerDto.class}, componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "created", source = "createdDate")
    @Mapping(target = "items", expression = "java(new ArrayList<ItemShortAnswerDto>())")
    @Named(value = "useMe")
    ItemRequestAnswerDto toDto(ItemRequest request);

    @IterableMapping(qualifiedByName = "useMe")
    List<ItemRequestAnswerDto> toDto(List<ItemRequest> requests);

}