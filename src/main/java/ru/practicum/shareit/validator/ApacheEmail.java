package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = ApacheEmailValidator.class)
@Documented
public @interface ApacheEmail {
    String message() default "email should be not empty and valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
