package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDto requestDto;
    private ProductResponseDto responseDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        requestDto = ProductRequestDto.builder()
                .name("Space Catnip")
                .description("High-quality catnip from the Andromeda galaxy.")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .categoryCode("TREATS")
                .build();

        product = new Product();
        product.setId(productId);
        product.setName("Space Catnip");
        product.setPrice(BigDecimal.TEN);
        product.setStock(100);

        responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("Space Catnip")
                .price(BigDecimal.TEN)
                .stock(100)
                .description("High-quality catnip from the Andromeda galaxy.")
                .currency("USD")
                .categoryCode("TREATS")
                .build();
    }

    @Test
    @DisplayName("createProduct: Should save product when name is unique")
    void createProduct_shouldSaveProduct_whenNameIsUnique() {
        when(mapper.toProduct(any(ProductRequestDto.class))).thenReturn(product);
        when(mapper.toProductDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto result = productService.createProduct(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Space Catnip");
        assertThat(result.getId()).isEqualTo(productId);

        verify(mapper, times(1)).toProduct(requestDto);
        verify(mapper, times(1)).toProductDto(product);
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

        when(mapper.toProduct(any(ProductRequestDto.class))).thenReturn(product);
        when(mapper.toProductDto(any(Product.class))).thenReturn(responseDto);
        productService.createProduct(requestDto);

        reset(mapper);

        assertThatThrownBy(() -> {
            productService.createProduct(duplicateRequest);
        })
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("Product with name 'Space Catnip' already exists");

        verify(mapper, never()).toProduct(any());
        verify(mapper, never()).toProductDto(any());
    }

    @Test
    @DisplayName("getProduct: Should return product when it exists")
    void getProduct_shouldReturnProduct_whenExists() {
        when(mapper.toProduct(any(ProductRequestDto.class))).thenReturn(product);
        when(mapper.toProductDto(any(Product.class))).thenReturn(responseDto);
        productService.createProduct(requestDto);

        reset(mapper);

        when(mapper.toProductDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.getProduct(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        verify(mapper, times(1)).toProductDto(product);
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
        when(mapper.toProduct(any(ProductRequestDto.class))).thenReturn(product);
        when(mapper.toProductDto(any(Product.class))).thenReturn(responseDto);
        productService.createProduct(requestDto);

        reset(mapper);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("New Space Catnip")
                .price(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto updatedResponseDto = ProductResponseDto.builder()
                .id(productId)
                .name("New Space Catnip")
                .price(BigDecimal.valueOf(20))
                .stock(100)
                .description("High-quality catnip from the Andromeda galaxy.")
                .currency("USD")
                .categoryCode("TREATS")
                .build();

        doNothing().when(mapper).merge(any(Product.class), any(ProductUpdateDto.class));
        when(mapper.toProductDto(any(Product.class))).thenReturn(updatedResponseDto);

        ProductResponseDto result = productService.updateProduct(productId, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Space Catnip");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(20));

        verify(mapper, times(1)).merge(product, updateDto);
        verify(mapper, times(1)).toProductDto(product);
    }

    @Test
    @DisplayName("updateProduct: Should throw ProductNotFoundException when product does not exist")
    void updateProduct_shouldThrowException_whenProductNotExists() {
        UUID nonExistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Doesn't matter")
                .build();

        assertThatThrownBy(() -> {
            productService.updateProduct(nonExistentId, updateDto);
        })
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product %s not found".formatted(nonExistentId));

        verify(mapper, never()).merge(any(), any());
        verify(mapper, never()).toProductDto(any());
    }

    @Test
    @DisplayName("updateProduct: Should throw DuplicateProductException when new name is already taken")
    void updateProduct_shouldThrowException_whenNameIsDuplicate() {
        when(mapper.toProduct(requestDto)).thenReturn(product);
        when(mapper.toProductDto(product)).thenReturn(responseDto);
        productService.createProduct(requestDto);

        ProductRequestDto requestDto2 = ProductRequestDto.builder()
                .name("Other Catnip")
                .description("desc")
                .price(BigDecimal.ONE)
                .currency("USD")
                .stock(1)
                .build();

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Other Catnip");

        when(mapper.toProduct(requestDto2)).thenReturn(product2);
        productService.createProduct(requestDto2);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Other Catnip")
                .build();

        assertThatThrownBy(() -> {
            productService.updateProduct(productId, updateDto);
        })
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("Product with name 'Other Catnip' already exists");
    }

    @Test
    @DisplayName("deleteProductById: Should successfully delete an existing product")
    void deleteProductById_shouldDeleteProduct_whenExists() {
        when(mapper.toProduct(any(ProductRequestDto.class))).thenReturn(product);
        productService.createProduct(requestDto);

        productService.deleteProductById(productId);

        assertThatThrownBy(() -> {
            productService.getProduct(productId);
        })
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProductById: Should complete silently when product does not exist")
    void deleteProductById_shouldDoNothing_whenNotExists() {
        UUID nonExistentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            productService.deleteProductById(nonExistentId);
        });
    }
}
