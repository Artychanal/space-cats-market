package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.dto.OrderRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderResponseDto;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import com.artur.java.spacecatsmarket.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping("/{number}")
    public ResponseEntity<OrderResponseDto> getByNumber(@PathVariable String number) {
        return ResponseEntity.ok(orderService.getByNumber(number));
    }

    @GetMapping("/reports/top-selling")
    public ResponseEntity<Page<ProductSalesProjection>> getTopSelling(Pageable pageable) {
        return ResponseEntity.ok(orderService.getTopSelling(pageable));
    }
}
