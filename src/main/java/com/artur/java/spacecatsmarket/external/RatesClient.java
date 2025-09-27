package com.artur.java.spacecatsmarket.external;

import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatesClient {

    private final RestClient ratesRestClient;

    public RateResponse getRate(String currency) {
        log.debug("Calling 3rd-party Rates for {}", currency);
        return ratesRestClient.get()
                .uri("/api/rates/{code}", currency)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new RatesClientException("Unknown currency in 3rd-party: " + currency);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RatesClientException("Rates service unavailable");
                })
                .body(RateResponse.class);
    }

    /** Runtime виняток — перехопимо глобальним хендлером і віддамо наш ErrorResponse */
    public static class RatesClientException extends RuntimeException {
        public RatesClientException(String message) { super(message); }
    }
}