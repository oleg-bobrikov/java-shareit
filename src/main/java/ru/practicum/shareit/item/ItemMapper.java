package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = User.class, componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    ItemDto toDto(Item item);

    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    List<ItemDto> toDtoList(List<Item> item);

    @Mapping(target = "owner", expression = "java(User.builder().id(itemDto.getOwnerId()).build())")
    @Mapping(target = "request", expression = "java(null)")
    Item toModel(ItemDto itemDto);
}