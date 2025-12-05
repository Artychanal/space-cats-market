package com.artur.java.spacecatsmarket.security;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import com.artur.java.spacecatsmarket.persistence.entity.ProductEntity;
import com.artur.java.spacecatsmarket.repository.CategoryRepository;
import com.artur.java.spacecatsmarket.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Value("${security.api-key}")
    private String apiKey;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        CategoryEntity category = new CategoryEntity();
        category.setCode("SECURE");
        category.setTitle("Secure Goods");
        CategoryEntity savedCategory = categoryRepository.save(category);

        ProductEntity product = ProductEntity.builder()
                .name("Shielded Tuna")
                .description("Protected delicacy")
                .price(BigDecimal.valueOf(10))
                .currency("USD")
                .stock(5)
                .category(savedCategory)
                .build();
        productRepository.save(product);
    }

    @Test
    @DisplayName("Should reject unauthorized requests when no credentials are provided")
    void shouldReturn401_whenNoCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow access with valid JWT containing roles claim")
    void shouldAllowAccessWithJwt() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should allow access with valid API key header")
    void shouldAllowAccessWithApiKey() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("X-API-KEY", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should return 401 for invalid API key value")
    void shouldRejectInvalidApiKey() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("X-API-KEY", "invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid API key provided"));
    }
}
