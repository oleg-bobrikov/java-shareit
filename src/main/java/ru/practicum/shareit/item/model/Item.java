package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean isAvailable;
    private ItemRequest request;
}
