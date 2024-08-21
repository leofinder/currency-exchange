package com.craftelix.dto;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(
        Integer id,
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate) {
}
