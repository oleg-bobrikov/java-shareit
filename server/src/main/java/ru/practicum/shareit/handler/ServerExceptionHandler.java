package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ServerExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(NotAvailableException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .exception(exception.getClass().toString())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handle(DataIntegrityViolationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .exception(exception.getClass().toString())
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handle(NotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(HttpStatus.NOT_FOUND.toString())
                .status(HttpStatus.NOT_FOUND.value())
                .exception(exception.getClass().toString())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handle(UnsupportedStatusException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .exception(exception.getClass().toString())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handle(Throwable exception) {
        log.error(exception.getMessage(), exception);
        return ResponseError.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(exception.getClass().toString())
                .message(exception.getMessage())
                .build();
    }
}

