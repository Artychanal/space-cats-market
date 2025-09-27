package com.artur.java.spacecatsmarket.dto;

import com.artur.java.spacecatsmarket.dto.validation.*;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

@Value @Builder(toBuilder = true) @Jacksonized
@GroupSequence({ProductUpdateDto.class, ExtendedValidation.class})
public class ProductUpdateDto {
    @Size(max=120) @CosmicWordCheck(groups = ExtendedValidation.class)
    String name;
    @Size(max=2000) String description;
    @DecimalMin(value="0.01") BigDecimal price;
    @ValidCurrency(groups = ExtendedValidation.class) String currency;
    @Min(0) Integer stock;
    @Size(max=32) String categoryCode;
}