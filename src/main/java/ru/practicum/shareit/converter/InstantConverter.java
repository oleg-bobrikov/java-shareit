package ru.practicum.shareit.converter;

import java.time.Instant;
import java.util.TimeZone;

public class InstantConverter {
    private static final long offset = TimeZone.getDefault().getRawOffset();

    public static String toPattern(Instant date) {
        // postman expects local datetime instead of UTC
        // convert to local datetime
       return date.plusMillis(offset).toString().substring(0, 19); // remove "Z"
    }

    public static String toPatternMillis(Instant date) {
        // postman expects local datetime instead of UTC
        // convert to local datetime
        return date.plusMillis(offset).toString().substring(0, 23)+"Z";
    }

    public static Instant fromPattern(String dateByString) {
        // postman return local datetime instead of UTC
        // convert to UTC
        return Instant.parse(dateByString + "Z").minusMillis(offset);
    }
}
