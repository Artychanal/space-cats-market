package com.artur.java.spacecatsmarket;

import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class})
class SpaceCatsMarketApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainStartsApplication() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                SpaceCatsMarketApplication.main(new String[]{
                        "--spring.datasource.url=jdbc:tc:postgresql:16-alpine:///cosmo_cats",
                        "--spring.datasource.username=test",
                        "--spring.datasource.password=test"
                }));
    }
}
