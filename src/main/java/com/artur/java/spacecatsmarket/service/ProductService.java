package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto request);
    ProductResponseDto getProduct(Long id);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    ProductResponseDto updateProduct(Long id, ProductUpdateDto request);
    void deleteProductById(Long id);
}
