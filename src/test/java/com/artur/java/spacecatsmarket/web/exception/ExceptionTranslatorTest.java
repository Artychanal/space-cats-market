package com.artur.java.spacecatsmarket.web.exception;

import com.artur.java.spacecatsmarket.external.exception.RateServiceException;
import com.artur.java.spacecatsmarket.service.exception.CategoryNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.OrderNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExceptionTranslatorTest.TestController.class)
@Import({ExceptionTranslator.class, ExceptionTranslatorTest.TestConfig.class})
class ExceptionTranslatorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("product not found -> 404 with path")
    void productNotFound() throws Exception {
        mockMvc.perform(get("/errors/product"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/errors/product"));
    }

    @Test
    @DisplayName("category/order not found -> 404")
    void domainNotFound() throws Exception {
        mockMvc.perform(get("/errors/category"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/errors/order"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("duplicate -> 409")
    void duplicate() throws Exception {
        mockMvc.perform(get("/errors/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("rate service -> 502")
    void rateService() throws Exception {
        mockMvc.perform(get("/errors/rates"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("failure"));
    }

    @Test
    @DisplayName("data integrity -> 409")
    void dataIntegrity() throws Exception {
        mockMvc.perform(get("/errors/data"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("generic -> 500")
    void generic() throws Exception {
        mockMvc.perform(get("/errors/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @RestController
    @RequestMapping("/errors")
    public static class TestController {

        @GetMapping("/product")
        void product() {
            throw new ProductNotFoundException("missing product");
        }

        @GetMapping("/category")
        void category() {
            throw new CategoryNotFoundException("missing category");
        }

        @GetMapping("/order")
        void order() {
            throw new OrderNotFoundException("missing order");
        }

        @GetMapping("/duplicate")
        void duplicate() {
            throw new DuplicateProductException("duplicate");
        }

        @GetMapping("/rates")
        void rates() {
            throw new RateServiceException("failure", new RuntimeException("boom"));
        }

        @GetMapping("/data")
        void data() {
            throw new DataIntegrityViolationException("constraint");
        }

        @GetMapping("/generic")
        void generic() {
            throw new RuntimeException("unexpected");
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        TestController testController() {
            return new TestController();
        }
    }
}
