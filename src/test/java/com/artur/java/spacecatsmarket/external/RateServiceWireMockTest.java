package com.artur.java.spacecatsmarket.external;

import com.artur.java.spacecatsmarket.SpaceCatsMarketApplication;
import com.artur.java.spacecatsmarket.config.PostgresTestConfig;
import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import com.artur.java.spacecatsmarket.external.exception.RateServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        classes = {SpaceCatsMarketApplication.class, PostgresTestConfig.class},
        properties = {
                "clients.rates.base-url=http://localhost:${wiremock.server.port}/api/v1",
                "clients.rates.connect-timeout-ms=1000",
                "clients.rates.read-timeout-ms=1500"
        })
@AutoConfigureWireMock(port = 0)
class RateServiceWireMockTest {

    @Autowired
    private RateService rateService;

    @Test
    @DisplayName("Should return rate when external API responds with 200 OK")
    void shouldReturnRate_whenExternalApiRespondsSuccessfully() {
        stubFor(get(urlEqualTo("/api/v1/rates/USD"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "code": "USD",
                                "rateToUAH": 39.10
                            }
                        """)));

        RateResponse response = rateService.getRate("USD");

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("USD");
        assertThat(response.rateToUAH()).isEqualByComparingTo(BigDecimal.valueOf(39.10));

        verify(exactly(1), getRequestedFor(urlEqualTo("/api/v1/rates/USD")));
    }

    @Test
    @DisplayName("Should throw RateServiceException when external API returns 404 Not Found")
    void shouldThrowException_whenExternalApiReturns404() {
        stubFor(get(urlEqualTo("/api/v1/rates/UNKNOWN"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "error": "Currency not found"
                            }
                        """)));

        assertThatThrownBy(() -> rateService.getRate("UNKNOWN"))
                .isInstanceOf(RateServiceException.class)
                .hasMessageContaining("Failed to retrieve currency rate for: UNKNOWN");

        verify(exactly(1), getRequestedFor(urlEqualTo("/api/v1/rates/UNKNOWN")));
    }

    @Test
    @DisplayName("Should throw RateServiceException when external API returns 500 Internal Server Error")
    void shouldThrowException_whenExternalApiReturns500() {
        stubFor(get(urlEqualTo("/api/v1/rates/EUR"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "error": "Internal server error"
                            }
                        """)));

        assertThatThrownBy(() -> rateService.getRate("EUR"))
                .isInstanceOf(RateServiceException.class)
                .hasMessageContaining("Failed to retrieve currency rate for: EUR");

        verify(exactly(1), getRequestedFor(urlEqualTo("/api/v1/rates/EUR")));
    }

    @Test
    @DisplayName("Should throw RateServiceException when external API returns malformed JSON")
    void shouldThrowException_whenExternalApiReturnsMalformedJson() {
        // Given
        stubFor(get(urlEqualTo("/api/v1/rates/JPY"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ invalid json }")));

        assertThatThrownBy(() -> rateService.getRate("JPY"))
                .isInstanceOf(RateServiceException.class);

        verify(exactly(1), getRequestedFor(urlEqualTo("/api/v1/rates/JPY")));
    }

    @Test
    @DisplayName("Should throw RateServiceException when external API connection times out")
    void shouldThrowException_whenConnectionTimesOut() {
        stubFor(get(urlEqualTo("/api/v1/rates/GBP"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "code": "GBP",
                                "rateToUAH": 50.00
                            }
                        """)
                        .withFixedDelay(2000)));

        assertThatThrownBy(() -> rateService.getRate("GBP"))
                .isInstanceOf(RateServiceException.class);

        verify(exactly(1), getRequestedFor(urlEqualTo("/api/v1/rates/GBP")));
    }
}
