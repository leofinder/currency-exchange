package com.craftelix.dto;

import java.math.BigDecimal;

public record CreateExchangeRateDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal rate) {
}
