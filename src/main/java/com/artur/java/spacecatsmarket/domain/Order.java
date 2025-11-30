package com.artur.java.spacecatsmarket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private String number;
    private String customerEmail;
    private OffsetDateTime createdAt;
    @Builder.Default
    private List<OrderLine> lines = new ArrayList<>();
}
