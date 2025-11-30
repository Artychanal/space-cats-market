package com.artur.java.spacecatsmarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class OrderLineRequestDto {
    @NotNull
    Long productId;

    @Min(1)
    int qty;

    @NotNull
    BigDecimal priceAtPurchase;
}
