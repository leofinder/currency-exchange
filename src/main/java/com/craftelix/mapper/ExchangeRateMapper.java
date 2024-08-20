package com.craftelix.mapper;

import com.craftelix.dto.CurrencyResponseDto;
import com.craftelix.dto.ExchangeRateResponseDto;
import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;

public class ExchangeRateMapper implements Mapper<ExchangeRate, ExchangeRateResponseDto> {

    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();

    private final Mapper<Currency, CurrencyResponseDto> currencyMapper = CurrencyMapper.getInstance();

    private ExchangeRateMapper() {

    }

    public static ExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ExchangeRateResponseDto mapFrom(ExchangeRate object) {
        return new ExchangeRateResponseDto(
                object.getId(),
                currencyMapper.mapFrom(object.getBaseCurrency()),
                currencyMapper.mapFrom(object.getTargetCurrency()),
                object.getRate()
        );
    }
}
