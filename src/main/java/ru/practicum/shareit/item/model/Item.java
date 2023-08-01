package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
