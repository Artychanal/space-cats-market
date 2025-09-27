package com.artur.java.spacecatsmarket.web.exception;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    int status;
    String error;
    String message;
    String path;
}