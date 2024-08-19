package com.craftelix.dto;

import java.math.BigDecimal;

public record ExchangeRateDto(
        Integer id,
        CurrencyDto baseCurrency,
        CurrencyDto targetCurrency,
        BigDecimal rate) {
}
