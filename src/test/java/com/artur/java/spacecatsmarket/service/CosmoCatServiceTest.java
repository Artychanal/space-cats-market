package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.service.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "feature.cosmoCats.enabled=true",
        "feature.kittyProducts.enabled=false"
})
class CosmoCatServiceTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    @DisplayName("Should return cosmo cats when feature is enabled")
    void getCosmoCats_shouldReturnCats_whenFeatureEnabled() {
        List<String> cats = cosmoCatService.getCosmoCats();

        assertThat(cats).isNotEmpty();
        assertThat(cats).contains("Nebula the Navigator", "Stardust the Explorer");
    }

    @Test
    @DisplayName("Should throw FeatureNotAvailableException when kittyProducts feature is disabled")
    void getKittyProducts_shouldThrowException_whenFeatureDisabled() {
        assertThatThrownBy(() -> cosmoCatService.getKittyProducts())
                .isInstanceOf(FeatureNotAvailableException.class)
                .hasMessageContaining("Feature 'kittyProducts' is not available");
    }
}