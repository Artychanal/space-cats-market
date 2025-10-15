package com.artur.java.spacecatsmarket.domain;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String number;
    private OffsetDateTime createdAt;
    private List<OrderLine> lines = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLine {
        private java.util.UUID productId;
        private int qty;
        private java.math.BigDecimal priceAtPurchase;
    }
}