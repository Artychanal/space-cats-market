package com.artur.java.spacecatsmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class CategoryRequestDto {
    @NotBlank
    @Size(max = 64)
    String code;

    @NotBlank
    @Size(max = 128)
    String title;
}
