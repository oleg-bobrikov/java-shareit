package ru.practicum.shareit.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class TimeStampValidator implements ConstraintValidator<TimeStamp, String> {
    @Override
    public void initialize(TimeStamp constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            Instant.parse(value + "Z");
        } catch (DateTimeParseException exception) {
            return false;
        }
        return true;
    }
}
