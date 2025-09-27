package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(@Valid @RequestBody ProductRequestDto request) {
        log.info("POST /api/products");
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponseDto get(@PathVariable UUID id) {
        log.debug("GET /api/products/{}", id);
        return service.get(id);
    }

    @GetMapping
    public List<ProductResponseDto> list(@RequestParam(defaultValue="0") int page,
                                         @RequestParam(defaultValue="20") int size) {
        return service.list(page, size);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateDto request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
