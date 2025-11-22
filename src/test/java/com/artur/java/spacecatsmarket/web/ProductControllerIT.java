package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.dto.*;
import com.artur.java.spacecatsmarket.service.CategoryService;
import com.artur.java.spacecatsmarket.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class},
        properties = "clients.rates.base-url=http://localhost/api/v1")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private ProductRequestDto validRequest;

    @BeforeEach
    void setUp() {
        categoryService.create(CategoryRequestDto.builder()
                .code("TREATS")
                .title("Space Treats")
                .build());

        validRequest = ProductRequestDto.builder()
                .name("Star Product")
                .description("Valid description")
                .price(BigDecimal.TEN)
                .currency("USD")
                .stock(100)
                .categoryCode("TREATS")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/products: Should return 201 Created when request is valid")
    void createProduct_shouldReturn201_whenRequestIsValid() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Star Product"))
                .andReturn();

        ProductResponseDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponseDto.class);
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getPrice()).isEqualByComparingTo(validRequest.getPrice());
    }

    @Test
    @DisplayName("POST /api/v1/products: Should return 400 Bad Request when name is blank")
    void createProduct_shouldReturn400_whenNameIsBlank() throws Exception {
        ProductRequestDto requestDto = validRequest.toBuilder()
                .name("")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name is mandatory"));
    }

    @Test
    @DisplayName("POST /api/v1/products: Should return 400 Bad Request when price is invalid")
    void createProduct_shouldReturn400_whenPriceIsInvalid() throws Exception {
        ProductRequestDto requestDto = validRequest.toBuilder()
                .price(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("price"))
                .andExpect(jsonPath("$.errors[0].message").value("Price must be greater than 0"));
    }

    @Test
    @DisplayName("POST /api/v1/products: Should return 400 Bad Request when stock is negative")
    void createProduct_shouldReturn400_whenStockIsNegative() throws Exception {
        ProductRequestDto requestDto = validRequest.toBuilder()
                .stock(-1)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("stock"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than or equal to 0"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: Should return 200 OK when product found")
    void getProductById_shouldReturn200_whenProductFound() throws Exception {
        ProductResponseDto created = productService.createProduct(validRequest);

        mockMvc.perform(get("/api/v1/products/{id}", created.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Star Product"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: Should return 404 Not Found when product does not exist")
    void getProductById_shouldReturn404_whenProductNotFound() throws Exception {
        long productId = 999L;

        mockMvc.perform(get("/api/v1/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product %s not found".formatted(productId)));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Should return 200 OK when request is valid")
    void updateProduct_shouldReturn200_whenRequestIsValid() throws Exception {
        ProductResponseDto created = productService.createProduct(validRequest);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Nebula Updated")
                .price(BigDecimal.valueOf(99.99))
                .build();

        MvcResult result = mockMvc.perform(put("/api/v1/products/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Nebula Updated"))
                .andReturn();

        ProductResponseDto updated = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponseDto.class);
        assertThat(updated.getPrice()).isEqualByComparingTo("99.99");

        ProductResponseDto persisted = productService.getProduct(created.getId());
        assertThat(persisted.getName()).isEqualTo("Nebula Updated");
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Should return 400 Bad Request when name is too long")
    void updateProduct_shouldReturn400_whenNameIsTooLong() throws Exception {
        ProductResponseDto created = productService.createProduct(validRequest);
        String longName = "a".repeat(121);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name(longName)
                .build();

        mockMvc.perform(put("/api/v1/products/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name cannot exceed 120 characters"));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id}: Should return 404 Not Found when product not found")
    void updateProduct_shouldReturn404_whenProductNotFound() throws Exception {
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Star Updated")
                .build();

        long productId = 999L;

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product %s not found".formatted(productId)));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id}: Should return 204 No Content on success")
    void deleteProduct_shouldReturn204_onSuccess() throws Exception {
        ProductResponseDto created = productService.createProduct(validRequest);

        mockMvc.perform(delete("/api/v1/products/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }
}
