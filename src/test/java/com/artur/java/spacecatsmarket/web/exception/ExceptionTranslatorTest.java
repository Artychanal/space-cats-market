package com.artur.java.spacecatsmarket.web.exception;

import com.artur.java.spacecatsmarket.external.exception.RateServiceException;
import com.artur.java.spacecatsmarket.service.exception.CategoryNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.DuplicateProductException;
import com.artur.java.spacecatsmarket.service.exception.OrderNotFoundException;
import com.artur.java.spacecatsmarket.service.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTranslatorTest {

    private ExceptionTranslator translator;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        translator = new ExceptionTranslator();
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setRequestURI("/api/test");
        request = mockReq;
    }

    @Test
    @DisplayName("handleNotFound should map product not found to 404")
    void handleNotFound() {
        ResponseEntity<ErrorResponse> response =
                translator.handleNotFound(new ProductNotFoundException("missing"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleDomainNotFound should cover category and order not found")
    void handleDomainNotFound() {
        ResponseEntity<ErrorResponse> categoryResp =
                translator.handleDomainNotFound(new CategoryNotFoundException("no category"), request);
        ResponseEntity<ErrorResponse> orderResp =
                translator.handleDomainNotFound(new OrderNotFoundException("no order"), request);

        assertThat(categoryResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(orderResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("handleDuplicate should return 409 Conflict")
    void handleDuplicate() {
        ResponseEntity<ErrorResponse> response = translator.handleDuplicate(
                new DuplicateProductException("duplicate"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("handleRates should translate RateServiceException to 502")
    void handleRates() {
        ResponseEntity<ErrorResponse> response = translator.handleRates(
                new RateServiceException("failure", new RuntimeException("boom")), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("failure");
    }

    @Test
    @DisplayName("handleDataIntegrity should return 409")
    void handleDataIntegrity() {
        ResponseEntity<ErrorResponse> response = translator.handleDataIntegrity(
                new DataIntegrityViolationException("constraint"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("constraint");
    }

    @Test
    @DisplayName("handleGeneric should return 500")
    void handleGeneric() {
        ResponseEntity<ErrorResponse> response = translator.handleGeneric(
                new RuntimeException("unexpected"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("unexpected");
    }
}
