package com.artur.java.spacecatsmarket.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProductResponseDto {
    UUID id;
    String name;
    String description;
    BigDecimal price;
    String currency;
    Integer stock;
    String categoryCode;
}