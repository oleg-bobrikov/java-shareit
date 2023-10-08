package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerExceptionHandlerTest {

    private ServerExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ServerExceptionHandler();
    }

    @Test
    public void handle_DataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals(409, responseError.getStatus());
    }

    @Test
    public void handle_NotFoundException() {
        // Arrange
        NotFoundException exception = Mockito.mock(NotFoundException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals(404, responseError.getStatus());
    }

    @Test
    public void handle_NotAvailableException() {
        // Arrange
        NotAvailableException exception = Mockito.mock(NotAvailableException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals(400, responseError.getStatus());
    }


    @Test
    public void handle_UnsupportedStatusException() {
        // Arrange
        UnsupportedStatusException exception = Mockito.mock(UnsupportedStatusException.class);

        // Act
        ResponseError responseError = exceptionHandler.handle(exception);

        // Assert
        assertEquals(400, responseError.getStatus());
    }

}
