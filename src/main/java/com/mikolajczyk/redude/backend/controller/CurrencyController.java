package com.mikolajczyk.redude.backend.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.controller.response.ApiResponse;
import com.mikolajczyk.redude.backend.controller.response.StatusResponse;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyEngine;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/currency")
@CrossOrigin("*")
public class CurrencyController {

    @GetMapping
    public ApiResponse getCurrencies() {
        log.info("Trying to get current currencies...");
        Map<String, Float> currencies = new HashMap<>();
        try {
            currencies.put("EUR", CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.EUR));
            currencies.put("GBP", CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.GBP));
            currencies.put("USD", CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.USD));
            log.info("SUCCESS");
            return new ApiResponse(currencies, StatusResponse.SUCCESS);
        } catch (UnirestException e) {
            log.info("ERROR WHILE GETTING CURRENT CURRENCIES");
            e.printStackTrace();
        }
        return new ApiResponse(null, StatusResponse.FAILED);
    }

    @GetMapping("/{code}")
    public ApiResponse getCurrency(@PathVariable CurrencyType code) {
        log.info("Trying to get current value of currency: " + code);
        try {
            float value = CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(code);
            log.info("SUCCESS");
            return new ApiResponse(value, StatusResponse.SUCCESS);
        } catch (UnirestException e) {
            log.info("ERROR WHILE GETTING CURRENT VALUE OF: " + code);
            e.printStackTrace();
        }
        return new ApiResponse(null, StatusResponse.FAILED);
    }
}
