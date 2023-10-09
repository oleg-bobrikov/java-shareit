package ru.practicum.shareit.handler;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorMessage {

    private String message;
    private String error;

}