package com.artur.java.spacecatsmarket.external;

import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import com.artur.java.spacecatsmarket.external.exception.RateServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "clients.rates.base-url=http://localhost:${wiremock.server.port}/api/v1")
@AutoConfigureWireMock(port = 0)
class RateServiceWireMockTest {

    @Autowired
    private RateService rateService;

    @Test
    void shouldReturnStubbedRate() {
        stubFor(get(urlEqualTo("/api/v1/rates/USD"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {"code":"USD","rateToUAH":39.10}
                """)));

        RateResponse response = rateService.getRate("USD");

        assertThat(response.code()).isEqualTo("USD");
        assertThat(response.rateToUAH()).isEqualByComparingTo("39.10");
        verify(getRequestedFor(urlEqualTo("/api/v1/rates/USD")));
    }

    @Test
    void shouldThrowExceptionWhenWireMockReturnsError() {
        stubFor(get(urlEqualTo("/api/v1/rates/EUR"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"not found\"}")));

        assertThatThrownBy(() -> rateService.getRate("EUR"))
                .isInstanceOf(RateServiceException.class)
                .hasMessageContaining("Failed to retrieve currency rate for: EUR");

        verify(getRequestedFor(urlEqualTo("/api/v1/rates/EUR")));
    }
}