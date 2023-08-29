package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = User.class, componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    @Mapping(target="available", source="item.isAvailable")
    ItemDto toDto(Item item);

    List<ItemDto> toDtoList(List<Item> items);

    @Mapping(target = "owner", expression = "java(User.builder().id(itemDto.getOwnerId()).build())")
    @Mapping(target = "itemRequest", expression = "java(null)")
    @Mapping(target="isAvailable", source="itemDto.available")
    Item toModel(ItemDto itemDto);
}
