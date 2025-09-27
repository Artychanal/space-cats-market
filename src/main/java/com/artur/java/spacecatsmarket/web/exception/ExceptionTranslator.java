package com.artur.java.spacecatsmarket.web.exception;

import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    record Violation(String fieldName, String reason) { @Builder public Violation{} }

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleNotFound(ProductNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("product-not-found"));
        pd.setTitle("Product Not Found");
        pd.setProperty("status", 404);
        pd.setProperty("error", "Not Found");
        pd.setProperty("message", ex.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @Override @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        List<Violation> vs = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new Violation(f.getField(), f.getDefaultMessage())).toList();

        String detail = vs.stream().map(v -> "Field '%s' %s".formatted(v.fieldName(), v.reason()))
                .reduce((a,b)->a+"; "+b).orElse("Validation failed");

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(BAD_REQUEST, detail);
        pd.setType(URI.create("validation-error"));
        pd.setTitle("Bad Request");

        String path = request.getDescription(false).replace("uri=", "");
        pd.setProperty("status", 400);
        pd.setProperty("error", "Bad Request");
        pd.setProperty("message", detail);
        pd.setProperty("path", path);
        pd.setProperty("violations", vs);

        return ResponseEntity.status(BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(pd);
    }
}
