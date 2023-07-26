package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private Integer id;
    @Email(message = "invalid email address")
    private String name;
    private String email;
}
