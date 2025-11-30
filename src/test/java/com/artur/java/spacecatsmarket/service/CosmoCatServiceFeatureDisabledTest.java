package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.service.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class})
@TestPropertySource(properties = {
        "feature.cosmo-cats-enabled=false",
        "feature.kitty-products-enabled=true"
})
class CosmoCatServiceFeatureDisabledTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    @DisplayName("Should throw exception when cosmoCats feature is disabled")
    void getCosmoCats_shouldThrowException_whenFeatureDisabled() {
        assertThatThrownBy(() -> cosmoCatService.getCosmoCats())
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessageContaining("Feature 'cosmoCats' is not available");
    }

    @Test
    @DisplayName("Should return kitty products when feature is enabled")
    void getKittyProducts_shouldReturnProducts_whenFeatureEnabled() {
        List<String> products = cosmoCatService.getKittyProducts();

        assertThat(products).isNotEmpty();
        assertThat(products).contains("Space Catnip", "Cosmic Scratching Post");
    }
}
