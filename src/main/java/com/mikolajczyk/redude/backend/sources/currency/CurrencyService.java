package com.mikolajczyk.redude.backend.sources.currency;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    public float getCurrentValueOfCurrency(CurrencyType currencyType) throws UnirestException {
        return CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(currencyType);
    }
}
