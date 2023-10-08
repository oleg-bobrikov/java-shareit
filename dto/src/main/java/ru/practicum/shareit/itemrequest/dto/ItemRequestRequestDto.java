package ru.practicum.shareit.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestRequestDto {
    @NotEmpty
    private String description;
}
