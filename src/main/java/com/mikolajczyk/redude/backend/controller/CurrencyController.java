package com.mikolajczyk.redude.backend.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.controller.response.ApiResponse;
import com.mikolajczyk.redude.backend.controller.response.StatusResponse;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyEngine;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyService;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1/currency")
@CrossOrigin("*")
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping
    public Map<String, Float> getCurrencies() {
        log.info("Trying to get current currencies...");
        Map<String, Float> currencies = new HashMap<>();
        try {
            currencies.put("EUR", service.getCurrentValueOfCurrency(CurrencyType.EUR));
            currencies.put("GBP", service.getCurrentValueOfCurrency(CurrencyType.GBP));
            currencies.put("USD", service.getCurrentValueOfCurrency(CurrencyType.USD));
            log.info("SUCCESS");
            return currencies;
        } catch (UnirestException e) {
            log.info("ERROR WHILE GETTING CURRENT CURRENCIES");
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @GetMapping("/{code}")
    public Float getCurrency(@PathVariable CurrencyType code) {
        log.info("Trying to get current value of currency: " + code);
        try {
            float value = service.getCurrentValueOfCurrency(code);
            log.info("SUCCESS");
            return value;
        } catch (UnirestException e) {
            log.info("ERROR WHILE GETTING CURRENT VALUE OF: " + code);
            e.printStackTrace();
        }
        return 0f;
    }
}
