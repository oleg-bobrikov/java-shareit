package ru.practicum.shareit.itemrequest;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.itemrequest.dto.AnswerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {User.class, InstantConverter.class, Item.class, ItemShortAnswerDto.class}, componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "created", expression = "java(InstantConverter.toPattern(request.getCreatedDate().toInstant()))")
    @Mapping(target = "items", expression = "java(new ArrayList<ItemShortAnswerDto>())")
    @Named(value = "useMe")
    AnswerDto toDto(ItemRequest request);

    @IterableMapping(qualifiedByName = "useMe")
    List<AnswerDto> toDto(List<ItemRequest> requests);

    @Mapping(target = "created", expression = "java(InstantConverter.toPattern(request.getCreatedDate().toInstant()))")
    @Mapping(target = "items", expression = "java(new ArrayList<ItemShortAnswerDto>())")
    AnswerDto toShortDto(ItemRequest request);

    List<AnswerDto> toShortDto(List<ItemRequest> requests);

}