package com.artur.java.spacecatsmarket.service.exception;

public class FeatureNotAvailableException extends RuntimeException {

    public FeatureNotAvailableException(String message) {
        super(message);
    }
}