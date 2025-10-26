package com.artur.java.spacecatsmarket.web.exception;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ErrorResponse {
    int status;
    String error;
    String message;
    String path;
    List<FieldErrorDto> errors;
}