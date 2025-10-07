package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;
    private final Map<UUID, Product> storage = new ConcurrentHashMap<>();

    private boolean nameExists(String name, UUID excludeId) {
        if (name == null) return false;
        String key = name.trim().toLowerCase();
        return storage.values().stream()
                .anyMatch(p -> !Objects.equals(p.getId(), excludeId)
                        && p.getName() != null
                        && p.getName().trim().toLowerCase().equals(key));
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto req) {
        if (nameExists(req.getName(), null)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists".formatted(req.getName()));
        }
        Product p = mapper.toProduct(req);
        storage.put(p.getId(), p);
        log.info("Created product id={} name={}", p.getId(), p.getName());
        return mapper.toProductDto(p);
    }

    @Override
    public ProductResponseDto get(UUID id) {
        Product p = storage.get(id);
        if (p == null) {
            log.warn("Product {} not found", id);
            throw new ProductNotFoundException("Product %s not found".formatted(id));
        }
        return mapper.toProductDto(p);
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        List<Product> all = new ArrayList<>(storage.values());
        Comparator<Product> comparator = buildComparator(pageable.getSort());
        if (comparator != null) {
            all.sort(comparator);
        }
        int from = (int) Math.min((long) pageable.getPageNumber() * pageable.getPageSize(), all.size());
        int to   = Math.min(from + pageable.getPageSize(), all.size());
        List<Product> pageSlice = (from >= to) ? List.of() : all.subList(from, to);
        List<ProductResponseDto> content = pageSlice.stream()
                .map(mapper::toProductDto)
                .toList();
        return new PageImpl<>(content, pageable, all.size());
    }
    private Comparator<Product> buildComparator(Sort sort) {
        if (sort == null || sort.isEmpty()) return null;

        Comparator<Product> result = null;
        for (Sort.Order order : sort) {
            Comparator<Product> c = switch (order.getProperty()) {
                case "name"         -> Comparator.comparing(p -> nullSafe(p.getName()));
                case "price"        -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                case "currency"     -> Comparator.comparing(p -> nullSafe(p.getCurrency()));
                case "stock"        -> Comparator.comparing(p -> Optional.ofNullable(p.getStock()).orElse(0));
                case "categoryCode" -> Comparator.comparing(p -> nullSafe(p.getCategoryCode()));
                case "id"           -> Comparator.comparing(Product::getId);
                default -> null;
            };
            if (c == null) continue;
            if (order.isDescending()) c = c.reversed();
            result = (result == null) ? c : result.thenComparing(c);
        }
        return result;
    }
    private static String nullSafe(String s) { return s == null ? "" : s; }

    @Override
    public ProductResponseDto update(UUID id, ProductUpdateDto req) {
        Product p = storage.get(id);
        if (p == null) throw new ProductNotFoundException("Product %s not found".formatted(id));
        if (req.getName() != null && nameExists(req.getName(), id)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists".formatted(req.getName()));
        }
        mapper.updateEntity(p, req);
        log.info("Updated product id={}", id);
        return mapper.toProductDto(p);
    }

    @Override
    public void delete(UUID id) {
        Product removed = storage.remove(id);
        if (removed == null) {
            log.warn("Delete called for non-existing product {}", id);
        } else {
            log.info("Deleted product id={}", id);
        }
    }
}
