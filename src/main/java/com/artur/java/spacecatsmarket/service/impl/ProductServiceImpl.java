package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.mapper.ProductEntityMapper;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import com.artur.java.spacecatsmarket.persistence.entity.ProductEntity;
import com.artur.java.spacecatsmarket.repository.CategoryRepository;
import com.artur.java.spacecatsmarket.repository.ProductRepository;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.CategoryNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductEntityMapper productEntityMapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','API')")
    public ProductResponseDto createProduct(ProductRequestDto req) {
        CategoryEntity category = resolveCategory(req.getCategoryCode());
        if (productRepository.existsByNameIgnoreCaseAndCategory(req.getName(), category)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists in category %s".formatted(
                            req.getName(), category.getCode()));
        }
        Product domain = productMapper.toProduct(req);
        ProductEntity entity = productEntityMapper.toEntity(domain, category);
        ProductEntity saved = productRepository.save(entity);
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return productMapper.toProductDto(productEntityMapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER','API')")
    public ProductResponseDto getProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found".formatted(id)));
        return productMapper.toProductDto(productEntityMapper.toDomain(product));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER','API')")
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productEntityMapper::toDomain)
                .map(productMapper::toProductDto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','API')")
    public ProductResponseDto updateProduct(Long id, ProductUpdateDto req) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found".formatted(id)));

        CategoryEntity targetCategory = product.getCategory();
        if (req.getCategoryCode() != null) {
            targetCategory = resolveCategory(req.getCategoryCode());
        }
        if (req.getName() != null && targetCategory != null) {
            boolean exists = productRepository.existsByNameIgnoreCaseAndCategoryAndIdNot(
                    req.getName(), targetCategory, id);
            if (exists) {
                throw new DuplicateProductException(
                        "Product with name '%s' already exists in category %s".formatted(
                                req.getName(), targetCategory.getCode()));
            }
        }

        Product domain = productEntityMapper.toDomain(product);
        productMapper.merge(domain, req);
        domain.setCategoryCode(targetCategory.getCode());

        ProductEntity updated = productEntityMapper.toEntity(domain, targetCategory);
        productEntityMapper.merge(product, updated);
        product.setCategory(targetCategory);

        ProductEntity saved = productRepository.save(product);
        log.info("Updated product id={}", id);
        return productMapper.toProductDto(productEntityMapper.toDomain(saved));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            log.warn("Delete called for non-existing product {}", id);
            return;
        }
        productRepository.deleteById(id);
        log.info("Deleted product id={}", id);
    }

    private CategoryEntity resolveCategory(String categoryCode) {
        return categoryRepository.findByCodeIgnoreCase(categoryCode)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category %s not found".formatted(categoryCode)));
    }
}
