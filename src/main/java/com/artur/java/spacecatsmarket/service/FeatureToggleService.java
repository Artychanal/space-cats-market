package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.config.FeatureToggleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureToggleService {

    private final FeatureToggleProperties featureToggleProperties;

    public boolean isFeatureEnabled(String featureName) {
        boolean enabled = featureToggleProperties.isFeatureEnabled(featureName);
        log.debug("Feature '{}' is {}", featureName, enabled ? "ENABLED" : "DISABLED");
        return enabled;
    }
}