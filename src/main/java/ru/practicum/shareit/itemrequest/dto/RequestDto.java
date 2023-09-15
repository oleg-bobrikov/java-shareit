package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RequestDto {
    @NotEmpty
    private String description;
}
