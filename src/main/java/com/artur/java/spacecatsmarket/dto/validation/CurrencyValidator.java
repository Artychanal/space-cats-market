package com.artur.java.spacecatsmarket.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {
    private static final Set<String> ALLOWED = Set.of("USD", "EUR", "UAH");
    private static final Pattern P = Pattern.compile("^[A-Z]{3}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        return P.matcher(value).matches() && ALLOWED.contains(value);
    }
}