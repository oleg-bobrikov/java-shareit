package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.ApacheEmail;

@Data
@Builder
public class UserDto {

    private Integer id;
    private String name;

    @ApacheEmail
    private String email;
}
