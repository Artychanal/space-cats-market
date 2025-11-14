package com.artur.java.spacecatsmarket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {

    private boolean cosmoCatsEnabled;
    private boolean kittyProductsEnabled;

    public boolean isFeatureEnabled(String featureName) {
        return switch (featureName) {
            case "cosmoCats" -> cosmoCatsEnabled;
            case "kittyProducts" -> kittyProductsEnabled;
            default -> false;
        };
    }
}