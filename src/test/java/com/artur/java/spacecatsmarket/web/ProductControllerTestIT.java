package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.dto.ProductRequestDto;
import com.artur.java.spacecatsmarket.dto.ProductResponseDto;
import com.artur.java.spacecatsmarket.dto.ProductUpdateDto;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import com.artur.java.spacecatsmarket.web.exception.ExceptionTranslator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductController.class, ExceptionTranslator.class})
class ProductControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/v1/products: Positive scenario - Should return 201 Created when request is valid")
    void createProduct_shouldReturn201_whenRequestIsValid() throws Exception {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("Star Product")  // CHANGED: Added cosmic word "Star"
                .description("Valid description")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(UUID.randomUUID())
                .name("Star Product")  // CHANGED
                .description("Valid description")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .build();

        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Star Product"));  // CHANGED

        verify(productService, times(1)).createProduct(any(ProductRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/products: Negative scenario - Should return 400 Bad Request when name is blank")
    void createProduct_shouldReturn400_whenNameIsBlank() throws Exception {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("")
                .description("Valid description")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))  // FIXED typo
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name is mandatory"));

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("POST /api/v1/products: Negative scenario - Should return 400 Bad Request when price is invalid")
    void createProduct_shouldReturn400_whenPriceIsInvalid() throws Exception {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("Galaxy Product")  // CHANGED: Added cosmic word
                .description("Valid description")
                .price(BigDecimal.valueOf(0.00))
                .currency("USD")
                .stock(100)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("price"))
                .andExpect(jsonPath("$.errors[0].message").value("Price must be greater than 0"));

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("POST /api/v1/products: Negative scenario - Should return 400 Bad Request when stock is negative")
    void createProduct_shouldReturn400_whenStockIsNegative() throws Exception {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("Comet Product")  // CHANGED: Added cosmic word
                .description("Valid description")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(-1)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("stock"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than or equal to 0"));

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: Positive scenario - Should return 200 OK when product found")
    void getProductById_shouldReturn200_whenProductFound() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("Test Product")
                .build();

        when(productService.getProduct(eq(productId))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProduct(eq(productId));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: Negative scenario - Should return 404 Not Found when product not found")
    void getProductById_shouldReturn404_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.getProduct(eq(productId)))
                .thenThrow(new ProductNotFoundException("Product %s not found".formatted(productId)));

        mockMvc.perform(get("/api/v1/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product %s not found".formatted(productId)));

        verify(productService, times(1)).getProduct(eq(productId));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Positive scenario - Should return 200 OK when request is valid")
    void updateProduct_shouldReturn200_whenRequestIsValid() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Nebula Updated")  // CHANGED: Added cosmic word
                .price(BigDecimal.valueOf(99.99))
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("Nebula Updated")  // CHANGED
                .price(BigDecimal.valueOf(99.99))
                .build();

        when(productService.updateProduct(eq(productId), any(ProductUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Nebula Updated"));  // CHANGED

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Negative scenario - Should return 400 Bad Request when name is too long")
    void updateProduct_shouldReturn400_whenNameIsTooLong() throws Exception {
        UUID productId = UUID.randomUUID();
        String longName = "a".repeat(121);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name(longName)
                .build();

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name cannot exceed 120 characters"));  // Now matches

        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Negative scenario - Should return 404 Not Found when product not found")
    void updateProduct_shouldReturn404_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Star Updated")  // CHANGED: Added cosmic word
                .build();

        when(productService.updateProduct(eq(productId), any(ProductUpdateDto.class)))
                .thenThrow(new ProductNotFoundException("Product %s not found".formatted(productId)));

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductUpdateDto.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id}: Positive scenario - Should return 204 No Content on success")
    void deleteProduct_shouldReturn204_onSuccess() throws Exception {
        UUID productId = UUID.randomUUID();

        doNothing().when(productService).deleteProductById(eq(productId));

        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProductById(eq(productId));
    }
}