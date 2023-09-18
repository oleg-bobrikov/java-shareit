package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ItemRequestDto {
    @NotEmpty
    private String description;
}
