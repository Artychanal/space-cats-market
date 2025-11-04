package com.artur.java.spacecatsmarket.external;

import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import com.artur.java.spacecatsmarket.external.exception.RateServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateService {

    private final RestClient ratesRestClient;

    public RateResponse getRate(String currency) {
        log.debug("Calling 3rd-party Rates for {}", currency);
        try {
            return ratesRestClient.get()
                    .uri("/rates/{code}", currency)
                    .retrieve()
                    .body(RateResponse.class);
        } catch (RestClientException e) {
            log.error("Error fetching rates for currency {}: {}", currency, e.getMessage(), e);
            throw new RateServiceException("Failed to retrieve currency rate for: " + currency, e);
        }
    }
}