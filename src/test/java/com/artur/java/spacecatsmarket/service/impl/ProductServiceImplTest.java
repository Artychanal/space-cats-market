package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.mapper.ProductMapperImpl;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceImplTest {

    private ProductMapper mapper;
    private ProductServiceImpl productService;
    private ProductRequestDto requestDto;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapperImpl();
        productService = new ProductServiceImpl(mapper);

        requestDto = ProductRequestDto.builder()
                .name("Space Catnip")
                .description("High-quality catnip from the Andromeda galaxy.")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .categoryCode("TREATS")
                .build();
    }

    @Test
    @DisplayName("createProduct: Should save product when name is unique")
    void createProduct_shouldSaveProduct_whenNameIsUnique() {
        ProductResponseDto result = productService.createProduct(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Space Catnip");
        assertThat(result.getId()).isNotNull();

        ProductResponseDto fromStorage = productService.getProduct(result.getId());
        assertThat(fromStorage.getName()).isEqualTo("Space Catnip");
    }

    @Test
    @DisplayName("createProduct: Should throw DuplicateProductException when name already exists")
    void createProduct_shouldThrowException_whenNameExists() {
        ProductRequestDto duplicateRequest = ProductRequestDto.builder()
                .name("Space Catnip")
                .description("A different description")
                .price(BigDecimal.ONE)
                .currency("USD")
                .stock(1)
                .build();

        productService.createProduct(requestDto);

        assertThatThrownBy(() -> productService.createProduct(duplicateRequest))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("Product with name 'Space Catnip' already exists");
    }

    @Test
    @DisplayName("getProduct: Should return product when it exists")
    void getProduct_shouldReturnProduct_whenExists() {
        ProductResponseDto created = productService.createProduct(requestDto);
        ProductResponseDto result = productService.getProduct(created.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getName()).isEqualTo(requestDto.getName());
    }

    @Test
    @DisplayName("getProduct: Should throw ProductNotFoundException when product does not exist")
    void getProduct_shouldThrowException_whenNotExists() {
        UUID nonExistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        assertThatThrownBy(() -> {
            productService.getProduct(nonExistentId);
        })
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product %s not found".formatted(nonExistentId));
    }

    @Test
    @DisplayName("updateProduct: Should update product when it exists and name is unique")
    void updateProduct_shouldUpdateProduct_whenExistsAndNameIsUnique() {
        ProductResponseDto created = productService.createProduct(requestDto);
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("New Space Catnip")
                .price(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto updatedResponseDto = ProductResponseDto.builder()
                .id(created.getId())
                .name("New Space Catnip")
                .price(BigDecimal.valueOf(20))
                .stock(100)
                .description("High-quality catnip from the Andromeda galaxy.")
                .currency("USD")
                .categoryCode("TREATS")
                .build();

        ProductResponseDto result = productService.updateProduct(created.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updatedResponseDto.getName());
        assertThat(result.getPrice()).isEqualTo(updatedResponseDto.getPrice());

        ProductResponseDto persisted = productService.getProduct(created.getId());
        assertThat(persisted.getName()).isEqualTo("New Space Catnip");
        assertThat(persisted.getPrice()).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    @DisplayName("updateProduct: Should throw ProductNotFoundException when product does not exist")
    void updateProduct_shouldThrowException_whenProductNotExists() {
        UUID nonExistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Doesn't matter")
                .build();

        assertThatThrownBy(() -> productService.updateProduct(nonExistentId, updateDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product %s not found".formatted(nonExistentId));
    }

    @Test
    @DisplayName("updateProduct: Should throw DuplicateProductException when new name is already taken")
    void updateProduct_shouldThrowException_whenNameIsDuplicate() {
        ProductResponseDto created = productService.createProduct(requestDto);

        ProductRequestDto requestDto2 = ProductRequestDto.builder()
                .name("Other Catnip")
                .description("desc")
                .price(BigDecimal.ONE)
                .currency("USD")
                .stock(1)
                .build();

        productService.createProduct(requestDto2);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Other Catnip")
                .build();

        assertThatThrownBy(() -> productService.updateProduct(created.getId(), updateDto))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("Product with name 'Other Catnip' already exists");
    }

    @Test
    @DisplayName("deleteProductById: Should successfully delete an existing product")
    void deleteProductById_shouldDeleteProduct_whenExists() {
        ProductResponseDto created = productService.createProduct(requestDto);

        productService.deleteProductById(created.getId());

        assertThatThrownBy(() -> productService.getProduct(created.getId()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProductById: Should complete silently when product does not exist")
    void deleteProductById_shouldDoNothing_whenNotExists() {
        UUID nonExistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> productService.deleteProductById(nonExistentId));
    }
}
