package com.mikolajczyk.redude.backend.currency;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyEngine;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CurrencyEngineTestSuite {

    @Test
    public void testGetEuro() {
        //Given
        float usd = 0f;
        float eur = 0f;
        float gbp = 0f;
        //When
        try {
            usd = CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.USD);
            eur = CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.EUR);
            gbp = CurrencyEngine.INSTANCE.getCurrentValueOfCurrency(CurrencyType.GBP);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        //Then
        assertTrue(usd > 0f);
        assertTrue(eur > 0f);
        assertTrue(gbp > 0f);
    }
}
