package com.artur.java.spacecatsmarket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private String apiKeyHeader = "X-API-KEY";
    private String apiKey = "cosmo-cats-api-key";
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "cosmo-cats-jwt-secret-please-rotate";
        private String jwsAlgorithm = "HS256";
        private String rolesClaim = "roles";
    }
}
