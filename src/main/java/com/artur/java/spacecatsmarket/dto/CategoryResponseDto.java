package com.artur.java.spacecatsmarket.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class CategoryResponseDto {
    Long id;
    String code;
    String title;
}
