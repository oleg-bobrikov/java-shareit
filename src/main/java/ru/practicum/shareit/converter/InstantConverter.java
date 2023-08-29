package ru.practicum.shareit.converter;

import java.time.Instant;

public class InstantConverter {

    public static String toPattern(Instant date) {
        return date.toString().substring(0,19); // remove "Z"
    }

    public static Instant fromPattern(String dateByString) {
        return Instant.parse(dateByString + "Z");
    }
}
