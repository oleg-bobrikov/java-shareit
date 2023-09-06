package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseError handle(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("BAD REQUEST")
                .timestamp(LocalDateTime.now())
                .status(400)
                .exception("org.springframework.web.bind.MethodArgumentNotValidException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(NotAvailableException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("BAD REQUEST")
                .timestamp(LocalDateTime.now())
                .status(400)
                .exception("ru.practicum.shareit.exception.NotAvailableException")
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError DataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("CONFLICT")
                .timestamp(LocalDateTime.now())
                .status(409)
                .exception("org.springframework.dao.DataIntegrityViolationException")
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handle(NotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("NOT FOUND")
                .status(404)
                .exception("ru.practicum.shareit.exception.NotFoundException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(PeriodValidationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("BAD REQUEST")
                .status(400)
                .exception("ru.practicum.shareit.exception.PeriodValidationException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(UnsupportedStateException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(exception.getMessage())
                .status(400)
                .exception("ru.practicum.shareit.exception.UnsupportedStateException")
                .message(exception.getMessage())
                .build();
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(UnsupportedStatusException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(exception.getMessage())
                .status(400)
                .exception("ru.practicum.shareit.exception.UnsupportedStatusException")
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error("BAD REQUEST")
                .status(400)
                .exception("ru.practicum.shareit.exception.ResponseError")
                .message(exception.getMessage())
                .build();
    }
}
