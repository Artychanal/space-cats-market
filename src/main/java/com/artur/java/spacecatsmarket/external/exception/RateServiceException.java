package com.artur.java.spacecatsmarket.external.exception;

public class RateServiceException extends RuntimeException {
    public RateServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}