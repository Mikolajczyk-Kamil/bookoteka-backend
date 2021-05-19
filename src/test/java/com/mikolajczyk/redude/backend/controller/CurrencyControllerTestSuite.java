package com.mikolajczyk.redude.backend.controller;

import com.mikolajczyk.redude.backend.sources.currency.CurrencyEngine;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyService;
import com.mikolajczyk.redude.backend.sources.currency.CurrencyType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTestSuite {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService service;

    @Test
    public void shouldFetchAllCurrencies() throws Exception {
        //Given
        when(service.getCurrentValueOfCurrency(CurrencyType.EUR)).thenReturn(4.5f);
        when(service.getCurrentValueOfCurrency(CurrencyType.USD)).thenReturn(3.5f);
        when(service.getCurrentValueOfCurrency(CurrencyType.GBP)).thenReturn(5f);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.EUR", Matchers.is(4.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.GBP", Matchers.is(5.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.USD", Matchers.is(3.5)));
    }

    @Test
    public void shouldFetchCurrencyEur() throws Exception {
        //Given
        when(service.getCurrentValueOfCurrency(CurrencyType.EUR)).thenReturn(4.5f);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/EUR"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(4.5)));
    }

    @Test
    public void shouldFetchCurrencyGbp() throws Exception {
        //Given
        when(service.getCurrentValueOfCurrency(CurrencyType.GBP)).thenReturn(5f);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/GBP"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(5.0)));
    }

    @Test
    public void shouldFetchCurrencyUsd() throws Exception {
        //Given
        when(service.getCurrentValueOfCurrency(CurrencyType.USD)).thenReturn(3.5f);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/USD"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(3.5)));
    }

    @Test
    public void shouldFetchFailed() throws Exception {
        //Given & When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/currency/PLN"))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }
}
