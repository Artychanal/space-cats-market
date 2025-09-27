package com.artur.java.spacecatsmarket.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {
    private static final Set<String> TERMS = Set.of("star","galaxy","comet","nebula","cosmo","orbit","astro");
    @Override public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotBlank в default-групі
        String lower = value.toLowerCase();
        return TERMS.stream().anyMatch(lower::contains);
    }
}