package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper = new com.artur.java.spacecatsmarket.mapper.ProductMapperImpl();
    private final Map<UUID, Product> storage = new ConcurrentHashMap<>();

    @Override
    public ProductResponseDto create(ProductRequestDto req) {
        Product p = mapper.toEntity(req);
        storage.put(p.getId(), p);
        log.info("Created product id={} name={}", p.getId(), p.getName());
        return mapper.toDto(p);
    }

    @Override
    public ProductResponseDto get(UUID id) {
        Product p = storage.get(id);
        if (p == null) {
            log.warn("Product {} not found", id);
            throw new ProductNotFoundException("Product %s not found".formatted(id));
        }
        return mapper.toDto(p);
    }

    @Override
    public List<ProductResponseDto> list(int page, int size) {
        List<Product> all = new ArrayList<>(storage.values());
        int from = Math.max(0, page * size), to = Math.min(all.size(), from + size);
        return (from >= to) ? List.of() : all.subList(from, to).stream().map(mapper::toDto).toList();
    }

    @Override
    public ProductResponseDto update(UUID id, ProductUpdateDto req) {
        Product p = storage.get(id);
        if (p == null) throw new ProductNotFoundException("Product %s not found".formatted(id));
        mapper.updateEntity(p, req);
        log.info("Updated product id={}", id);
        return mapper.toDto(p);
    }

    @Override
    public void delete(UUID id) {
        if (storage.remove(id) == null) throw new ProductNotFoundException("Product %s not found".formatted(id));
        log.warn("Deleted product id={}", id);
    }
}
