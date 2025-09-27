package com.artur.java.spacecatsmarket.service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String msg){ super(msg); }
}