package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;
    private final Map<UUID, Product> storage = new ConcurrentHashMap<>();
    private final Map<String, UUID> nameToIdIndex = new ConcurrentHashMap<>();

    private boolean isProductExistsByName(String name, UUID excludeId) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        String key = name.trim().toLowerCase();
        UUID existingId = nameToIdIndex.get(key);
        return existingId != null && !existingId.equals(excludeId);
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto req) {
        if (isProductExistsByName(req.getName(), null)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists".formatted(req.getName()));
        }
        Product p = mapper.toProduct(req);
        storage.put(p.getId(), p);
        if (StringUtils.isNotBlank(p.getName())) {
            nameToIdIndex.put(p.getName().trim().toLowerCase(), p.getId());
        }
        log.info("Created product id={} name={}", p.getId(), p.getName());
        return mapper.toProductDto(p);
    }

    @Override
    public ProductResponseDto getProduct(UUID id) {
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

        Comparator<Product> comparator = getCachedComparator(pageable.getSort());
        if (comparator != null) {
            all.sort(comparator);
        }

        long total = all.size();
        int start = (int) Math.min(pageable.getOffset(), total);
        int end = (int) Math.min((long) start + pageable.getPageSize(), total);

        List<ProductResponseDto> content = all.subList(start, end).stream()
                .map(mapper::toProductDto)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    private static final Comparator<Product> NAME_COMPARATOR =
            Comparator.comparing(p -> StringUtils.defaultString(p.getName()));
    private static final Comparator<Product> PRICE_COMPARATOR =
            Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
    private static final Comparator<Product> CURRENCY_COMPARATOR =
            Comparator.comparing(p -> StringUtils.defaultString(p.getCurrency()));
    private static final Comparator<Product> STOCK_COMPARATOR =
            Comparator.comparing(p -> Optional.ofNullable(p.getStock()).orElse(0));
    private static final Comparator<Product> CATEGORY_CODE_COMPARATOR =
            Comparator.comparing(p -> StringUtils.defaultString(p.getCategoryCode()));
    private static final Comparator<Product> ID_COMPARATOR =
            Comparator.comparing(Product::getId);

    private static final Map<String, Comparator<Product>> ASCENDING = Map.ofEntries(
            Map.entry("name", NAME_COMPARATOR),
            Map.entry("price", PRICE_COMPARATOR),
            Map.entry("currency", CURRENCY_COMPARATOR),
            Map.entry("stock", STOCK_COMPARATOR),
            Map.entry("categoryCode", CATEGORY_CODE_COMPARATOR),
            Map.entry("id", ID_COMPARATOR)
    );

    private static final Map<String, Comparator<Product>> DESCENDING =
            ASCENDING.entrySet().stream()
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().reversed()));

    private static final ConcurrentMap<String, Comparator<Product>> COMPARATOR_CACHE = new ConcurrentHashMap<>();

    private static Comparator<Product> getCachedComparator(Sort sort) {
        if (sort == null || sort.isUnsorted()) return null;

        String key = sort.stream()
                .map(o -> o.getProperty() + ":" + (o.isDescending() ? "desc" : "asc"))
                .collect(Collectors.joining(","));

        return COMPARATOR_CACHE.computeIfAbsent(key, k ->
                sort.stream()
                        .map(o -> o.isDescending()
                                ? DESCENDING.get(o.getProperty())
                                : ASCENDING.get(o.getProperty()))
                        .filter(Objects::nonNull)
                        .reduce(Comparator::thenComparing)
                        .orElse(null)
        );
    }

    @Override
    public ProductResponseDto updateProduct(UUID id, ProductUpdateDto req) {
        Product p = storage.get(id);
        if (p == null) throw new ProductNotFoundException("Product %s not found".formatted(id));
        if (req.getName() != null && isProductExistsByName(req.getName(), id)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists".formatted(req.getName()));
        }
        String oldNameKey = (p.getName() != null) ? p.getName().trim().toLowerCase() : null;
        mapper.merge(p, req);
        String newNameKey = (p.getName() != null) ? p.getName().trim().toLowerCase() : null;
        if (!Objects.equals(oldNameKey, newNameKey)) {
            if (oldNameKey != null) {
                nameToIdIndex.remove(oldNameKey);
            }
            if (newNameKey != null) {
                nameToIdIndex.put(newNameKey, p.getId());
            }
        }
        log.info("Updated product id={}", id);
        return mapper.toProductDto(p);
    }

    @Override
    public void deleteProductById(UUID id) {
        Product removed = storage.remove(id);
        if (removed == null) {
            log.warn("Delete called for non-existing product {}", id);
        } else {
            if (StringUtils.isNotBlank(removed.getName())) {
                nameToIdIndex.remove(removed.getName().trim().toLowerCase());
            }
            log.info("Deleted product id={}", id);
        }
    }
}
