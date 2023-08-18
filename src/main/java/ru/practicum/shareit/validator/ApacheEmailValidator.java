package ru.practicum.shareit.validator;

import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ApacheEmailValidator implements ConstraintValidator<ApacheEmail, String> {
    @Override
    public void initialize(ApacheEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String emailAddress, ConstraintValidatorContext constraintValidatorContext) {
        return EmailValidator.getInstance().isValid(emailAddress);
    }
}
