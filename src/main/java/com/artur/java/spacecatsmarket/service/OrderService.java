package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.dto.OrderRequestDto;
import com.artur.java.spacecatsmarket.dto.OrderResponseDto;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto create(OrderRequestDto request);
    OrderResponseDto getByNumber(String number);
    Page<ProductSalesProjection> getTopSelling(Pageable pageable);
}
