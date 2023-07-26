package ru.practicum.shareit.handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ResponseError;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler{
    @ExceptionHandler({DuplicateEmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleDuplicateEmail(DuplicateEmailException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("CONFLICT")
                .timestamp(LocalDateTime.now())
                .status(409)
                .exception("ru.practicum.shareit.exception.DuplicateEmailException")
                .message(exception.getMessage())
                .build();
    }
}
