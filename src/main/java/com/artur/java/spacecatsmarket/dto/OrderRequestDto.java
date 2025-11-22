package com.artur.java.spacecatsmarket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class OrderRequestDto {
    @NotBlank
    String number;

    @Email
    @NotBlank
    String customerEmail;

    @Valid
    @NotEmpty
    List<OrderLineRequestDto> lines;
}
