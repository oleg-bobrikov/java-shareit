package ru.practicum.shareit.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;

public class TimeStampValidatorTest {

    private TimeStampValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        validator = new TimeStampValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    public void isValid_validDate_returnTrue() {
        // Arrange
        String validDate = "2023-09-23T12:00:00";

        // Act
        boolean isValid = validator.isValid(validDate, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void isValid_null_returnFalse() {
        // Act
        boolean isValid = validator.isValid(null, context);

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void isValid_invalidDateFormat_returnFalse() {
        // Arrange
        String invalidDate = "2023-09-23T12:00:00ZInvalid";

        // Act
        boolean isValid = validator.isValid(invalidDate, context);

        // Assert
        assertFalse(isValid);
    }
}
