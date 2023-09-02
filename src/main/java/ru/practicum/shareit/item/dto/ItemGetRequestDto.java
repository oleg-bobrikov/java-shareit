package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemGetRequestDto {

    @NotNull
    private Long id;

    private Long userId;

}
