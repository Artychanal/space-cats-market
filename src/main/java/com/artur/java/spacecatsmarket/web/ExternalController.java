package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.external.RatesClient;
import com.artur.java.spacecatsmarket.external.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalController {

    private final RatesClient rates;

    @GetMapping("/rates/{currency}")
    public RateResponse getRate(@PathVariable String currency) {
        return rates.getRate(currency);
    }
}
