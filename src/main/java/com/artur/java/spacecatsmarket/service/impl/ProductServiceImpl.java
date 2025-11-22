package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Category;
import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto req) {
        Category category = resolveCategory(req.getCategoryCode());
        if (productRepository.existsByNameIgnoreCaseAndCategory(req.getName(), category)) {
            throw new DuplicateProductException(
                    "Product with name '%s' already exists in category %s".formatted(
                            req.getName(), category.getCode()));
        }
        Product entity = mapper.toProduct(req);
        entity.setCategory(category);
        Product saved = productRepository.save(entity);
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return mapper.toProductDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found".formatted(id)));
        return mapper.toProductDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toProductDto);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductUpdateDto req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product %s not found".formatted(id)));

        if (req.getCategoryCode() != null) {
            product.setCategory(resolveCategory(req.getCategoryCode()));
        }
        if (req.getName() != null && product.getCategory() != null) {
            boolean exists = productRepository.existsByNameIgnoreCaseAndCategoryAndIdNot(
                    req.getName(), product.getCategory(), id);
            if (exists) {
                throw new DuplicateProductException(
                        "Product with name '%s' already exists in category %s".formatted(
                                req.getName(), product.getCategory().getCode()));
            }
        }

        mapper.merge(product, req);
        Product updated = productRepository.save(product);
        log.info("Updated product id={}", id);
        return mapper.toProductDto(updated);
    }

    @Override
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            log.warn("Delete called for non-existing product {}", id);
            return;
        }
        productRepository.deleteById(id);
        log.info("Deleted product id={}", id);
    }

    private Category resolveCategory(String categoryCode) {
        return categoryRepository.findByCodeIgnoreCase(categoryCode)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category %s not found".formatted(categoryCode)));
    }
}
