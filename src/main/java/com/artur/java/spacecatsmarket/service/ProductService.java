package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.dto.*;
import java.util.*;

public interface ProductService {
    ProductResponseDto create(ProductRequestDto request);
    ProductResponseDto get(UUID id);
    List<ProductResponseDto> list(int page, int size);
    ProductResponseDto update(UUID id, ProductUpdateDto request);
    void delete(UUID id);
}
