package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomExceptionHandlerTest {

    private CustomExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new CustomExceptionHandler();
    }

    @Test
    public void handle_MethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("BAD REQUEST", responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("org.springframework.web.bind.MethodArgumentNotValidException", responseError.getException());

    }

    @Test
    public void handle_DataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("CONFLICT", responseError.getError());
        assertEquals(409, responseError.getStatus());
        assertEquals("org.springframework.dao.DataIntegrityViolationException", responseError.getException());
    }

    @Test
    public void handle_NotFoundException() {
        // Arrange
        NotFoundException exception = Mockito.mock(NotFoundException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("NOT FOUND", responseError.getError());
        assertEquals(404, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.NotFoundException", responseError.getException());
    }

    @Test
    public void handle_NotAvailableException() {
        // Arrange
        NotAvailableException exception = Mockito.mock(NotAvailableException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("BAD REQUEST", responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.NotAvailableException", responseError.getException());
    }

    @Test
    public void handle_UnsupportedStateException() {
        // Arrange
        UnsupportedStateException exception = Mockito.mock(UnsupportedStateException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals(exception.getMessage(), responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.UnsupportedStateException", responseError.getException());
    }

    @Test
    public void handle_PeriodValidationException() {
        // Arrange
        PeriodValidationException exception = Mockito.mock(PeriodValidationException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("BAD REQUEST", responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.PeriodValidationException", responseError.getException());
    }

    @Test
    public void handle_UnsupportedStatusException() {
        // Arrange
        UnsupportedStatusException exception = Mockito.mock(UnsupportedStatusException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("BAD REQUEST", responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.UnsupportedStatusException", responseError.getException());
    }

    @Test
    public void handle_RuntimeException() {
        // Arrange
        RuntimeException exception = Mockito.mock(RuntimeException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals("BAD REQUEST", responseError.getError());
        assertEquals(400, responseError.getStatus());
        assertEquals("ru.practicum.shareit.exception.ResponseError", responseError.getException());
    }
}
