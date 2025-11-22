package com.artur.java.spacecatsmarket.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class OrderResponseDto {
    Long id;
    String number;
    String customerEmail;
    OffsetDateTime createdAt;
    List<OrderLineResponseDto> lines;
}
