package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BookingStateUnknownException;
import ru.practicum.shareit.exception.PeriodValidationException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BookingStateUnknownException.class})
    public ErrorMessage handle(BookingStateUnknownException exception) {
        log.debug("Получен статус 400 BAD REQUEST {}", exception.getMessage(), exception);
        return ErrorMessage.builder()
                .error(exception.getMessage())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            PeriodValidationException.class})
    public ErrorMessage handle(RuntimeException exception) {
        log.debug("Получен статус 400 BAD REQUEST {}", exception.getMessage(), exception);
        return ErrorMessage.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorMessage handle(Throwable exception) {
        log.debug("Получен статус 500 INTERNAL SERVER ERROR {}", exception.getMessage(), exception);
        return ErrorMessage.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message(exception.getMessage())
                .build();
    }
}