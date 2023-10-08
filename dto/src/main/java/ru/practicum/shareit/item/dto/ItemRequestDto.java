package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validator.OnCreate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotEmpty(groups = {OnCreate.class})
    private String name;

    @NotEmpty(groups = {OnCreate.class})
    private String description;

    @NotNull(groups = {OnCreate.class})
    private Boolean available;

    private Long requestId;
}
