package com.artur.java.spacecatsmarket.dto;

import com.artur.java.spacecatsmarket.dto.validation.*;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
@Jacksonized
@GroupSequence({ProductRequestDto.class, ExtendedValidation.class})
public class ProductRequestDto {
    @NotBlank(message = "Name is mandatory")
    @Size(max = 120, message = "Name cannot exceed 120 characters")
    @CosmicWordCheck(groups = ExtendedValidation.class)
    String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    BigDecimal price;

    @NotBlank(message = "Currency is mandatory")
    @ValidCurrency(groups = ExtendedValidation.class)
    String currency;

    @NotNull(message = "Stock is mandatory")
    @Min(0)
    Integer stock;

    @Size(max = 32)
    String categoryCode;
}