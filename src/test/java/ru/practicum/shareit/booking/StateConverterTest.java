package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.UnsupportedStateException;

import static org.junit.jupiter.api.Assertions.*;

class StateConverterTest {

    @Test
    void toState_unsupportedState_throwException() {
        // Arrange
        String invalidState = "INVALID_STATE";

        // Act and Assert

        assertThrows(UnsupportedStateException.class, () -> StateConverter.toState(invalidState));
    }

    @Test
    void toState_stateValid_returnState() {
        // Arrange
        String validState = "PAST";

        // Act
        State result = StateConverter.toState(validState);

        // Assert
        assertEquals(State.PAST, result);
    }
}