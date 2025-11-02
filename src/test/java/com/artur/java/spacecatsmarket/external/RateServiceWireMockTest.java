package com.artur.java.spacecatsmarket.external;

import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

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
    }
}