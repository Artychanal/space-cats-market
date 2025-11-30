package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.service.CategoryService;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class})
@DisplayName("Product Service Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductServiceImplTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private ProductRequestDto requestDto;

    @BeforeEach
    void setUp() {
        categoryService.createCategory(CategoryRequestDto.builder()
                .code("TREATS")
                .title("Space Treats")
                .build());

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
                .categoryCode("TREATS")
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
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> productService.getProduct(nonExistentId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product %s not found".formatted(nonExistentId));
    }

    @Test
    @DisplayName("updateProduct: Should update product when it exists and name is unique")
    void updateProduct_shouldUpdateProduct_whenExistsAndNameIsUnique() {
        // Given
        ProductResponseDto created = productService.createProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("New Space Catnip")
                .price(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto result = productService.updateProduct(created.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Space Catnip");
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));

        ProductResponseDto persisted = productService.getProduct(created.getId());
        assertThat(persisted.getName()).isEqualTo("New Space Catnip");
        assertThat(persisted.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    @DisplayName("updateProduct: Should throw ProductNotFoundException when product does not exist")
    void updateProduct_shouldThrowException_whenProductNotExists() {
        Long nonExistentId = 999L;
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
                .categoryCode("TREATS")
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
        Long nonExistentId = 999L;

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                productService.deleteProductById(nonExistentId)
        );
    }
}
