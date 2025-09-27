package com.artur.java.spacecatsmarket.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CosmicWordValidator.class)
public @interface CosmicWordCheck {
    String message() default "Name must contain a cosmic term (star, galaxy, comet, nebula)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}