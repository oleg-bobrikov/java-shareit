package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseError {
    private LocalDateTime timestamp;
    private String error;
    private String exception;
    private int status;
    private String message;
}
