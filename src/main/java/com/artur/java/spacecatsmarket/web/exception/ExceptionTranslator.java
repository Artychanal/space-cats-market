package com.artur.java.spacecatsmarket.web.exception;

import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotFoundException ex, HttpServletRequest req) {
        log.warn("NotFound at {} -> {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(404)
                        .error("Not Found")
                        .message(ex.getMessage())
                        .path(req.getRequestURI())
                        .errors(null)
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new FieldErrorDto(f.getField(), f.getDefaultMessage()))
                .toList();
        log.warn("Validation failed at {} -> {}", req.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("Bad Request")
                        .message("Validaton failed")
                        .path(req.getRequestURI())
                        .errors(fieldErrors)
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "?";
        String msg = "Failed to convert '%s' with value: '%s' (expected %s)"
                .formatted(ex.getName(), ex.getValue(), expected);
        log.warn("Type mismatch at {} -> {}", req.getRequestURI(), msg);
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("Bad Request")
                        .message(msg)
                        .path(req.getRequestURI())
                        .errors(null)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error at {} -> {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .status(500)
                        .error("Internal Server Error")
                        .message(ex.getMessage())
                        .path(req.getRequestURI())
                        .build()
        );
    }
    @ExceptionHandler(com.artur.java.spacecatsmarket.external.RatesClient.RatesClientException.class)
    public ResponseEntity<ErrorResponse> handleRates(
            com.artur.java.spacecatsmarket.external.RatesClient.RatesClientException ex,
            HttpServletRequest req) {
        log.error("RatesClient failure at {} -> {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ErrorResponse.builder()
                        .status(502)
                        .error("Bad Gateway")
                        .message(ex.getMessage())
                        .path(req.getRequestURI())
                        .errors(null)
                        .build()
        );
    }
    @ExceptionHandler(com.artur.java.spacecatsmarket.service.exception.DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            com.artur.java.spacecatsmarket.service.exception.DuplicateProductException ex,
            HttpServletRequest req) {
        log.warn("Duplicate product conflict at {} -> {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .status(409)
                        .error("Conflict")
                        .message(ex.getMessage())
                        .path(req.getRequestURI())
                        .errors(null)
                        .build()
        );
    }
}
