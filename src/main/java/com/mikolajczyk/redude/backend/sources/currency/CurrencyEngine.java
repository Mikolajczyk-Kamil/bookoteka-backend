package com.mikolajczyk.redude.backend.sources.currency;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum CurrencyEngine {

    INSTANCE;

    private Float eur;
    private Float gbp;
    private Float usd;
    private LocalDateTime lastCheck = null;

    public float getCurrentValueOfCurrency(CurrencyType currencyType) throws UnirestException {
        if (lastCheck == null) {
            lastCheck = LocalDateTime.now();
            loadData();
        }
        else if (ChronoUnit.HOURS.between(LocalDateTime.now(), lastCheck) > 6)
            loadData();
        if (currencyType.equals(CurrencyType.EUR))
            return eur;
        else if (currencyType.equals(CurrencyType.GBP))
            return gbp;
        else if (currencyType.equals(CurrencyType.USD))
            return usd;
        else
            return 0f;
    }

    private void loadData() throws UnirestException {
        eur = doRequest(CurrencyType.EUR);
        gbp = doRequest(CurrencyType.GBP);
        usd = doRequest(CurrencyType.USD);
        lastCheck = LocalDateTime.now();
    }

    private float doRequest(CurrencyType currencyType) throws UnirestException {
        String url = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/" + currencyType.toString().toLowerCase() + "/pln.json";
        HttpResponse<JsonNode> response = Unirest.get(url)
                .asJson();
        Double result = 0d;
        if (response.getBody().getObject().has("pln"))
            result = response.getBody().getObject().getDouble("pln");
        return result.floatValue();
    }
}
