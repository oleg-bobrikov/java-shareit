package ru.practicum.shareit.converter;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstantConverterTest {
    private static final long offset = TimeZone.getDefault().getRawOffset();

    @Test
    public void toPattern_validDate_convert() {
        // Arrange
        Instant input = Instant.parse("2023-09-23T12:00:00Z");
        String expected = input.plusMillis(offset).toString().substring(0, 19);

        // Act
        String result = InstantConverter.toPattern(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void toPatternMillis_validDate_convert() {
        // Arrange
        Instant input = Instant.parse("2023-09-23T12:00:00.123456789Z");
        String expected = input.plusMillis(offset).toString().substring(0, 23) + "Z";

        // Act
        String result = InstantConverter.toPatternMillis(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void fromPattern_validDate_convert() {
        // Arrange
        String input = "2023-09-23T12:00:00";
        Instant expected = Instant.parse("2023-09-23T12:00:00Z").minusMillis(offset);

        // Act
        Instant result = InstantConverter.fromPattern(input);

        // Assert
        assertEquals(expected, result);
    }
}
