package com.artur.java.spacecatsmarket.config;

import com.artur.java.spacecatsmarket.mapper.ProductMapper;
import com.artur.java.spacecatsmarket.mapper.ProductMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MappersTestConfig {

    @Bean
    public ProductMapper productMapper() {
        return new ProductMapperImpl();
    }
}