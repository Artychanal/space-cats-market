package com.artur.java.spacecatsmarket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {
    private Long id;
    private Long productId;
    private int qty;
    private BigDecimal priceAtPurchase;
}
