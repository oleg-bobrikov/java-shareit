package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean isAvailable;
    private Integer requestId;
}
