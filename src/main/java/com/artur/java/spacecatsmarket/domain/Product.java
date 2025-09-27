package com.artur.java.spacecatsmarket.domain;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Integer stock;
    private String categoryCode;
}