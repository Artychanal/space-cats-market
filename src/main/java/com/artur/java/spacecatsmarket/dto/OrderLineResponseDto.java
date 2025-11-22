package com.artur.java.spacecatsmarket.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class OrderLineResponseDto {
    Long productId;
    int qty;
    BigDecimal priceAtPurchase;
}
