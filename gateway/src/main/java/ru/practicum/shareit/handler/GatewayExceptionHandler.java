package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BookingStateUnknownException;
import ru.practicum.shareit.exception.PeriodValidationException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GatewayExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handle(MethodArgumentNotValidException exception) {
        return ErrorMessage.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorMessage handle(ConstraintViolationException exception) {
        return ErrorMessage.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PeriodValidationException.class)
    public ErrorMessage handle(PeriodValidationException exception) {
        return ErrorMessage.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BookingStateUnknownException.class})
    public ErrorMessage handle(BookingStateUnknownException exception) {
        return ErrorMessage.builder()
                .error(exception.getMessage())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorMessage handle(RuntimeException exception) {
        return ErrorMessage.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message(exception.getMessage())
                .build();
    }
}