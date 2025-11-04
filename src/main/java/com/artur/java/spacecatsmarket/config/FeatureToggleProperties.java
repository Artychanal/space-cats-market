package com.artur.java.spacecatsmarket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {

    private Map<String, FeatureConfig> features = new HashMap<>();

    @Getter
    @Setter
    public static class FeatureConfig {
        private boolean enabled;
    }

    public boolean isFeatureEnabled(String featureName) {
        FeatureConfig config = features.get(featureName);
        return config != null && config.isEnabled();
    }
}