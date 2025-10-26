package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.*;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto request);
    ProductResponseDto getProduct(UUID id);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    ProductResponseDto updateProduct(UUID id, ProductUpdateDto request);
    void deleteProductById(UUID id);
}
