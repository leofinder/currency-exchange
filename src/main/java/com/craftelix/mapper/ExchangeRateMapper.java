package com.craftelix.mapper;

import com.craftelix.dto.CurrencyDto;
import com.craftelix.dto.ExchangeRateDto;
import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;

public class ExchangeRateMapper implements Mapper<ExchangeRate, ExchangeRateDto> {

    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();

    private final Mapper<Currency, CurrencyDto> currencyMapper = CurrencyMapper.getInstance();

    private ExchangeRateMapper() {

    }

    public static ExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ExchangeRateDto mapFrom(ExchangeRate object) {
        return new ExchangeRateDto(
                object.getId(),
                currencyMapper.mapFrom(object.getBaseCurrency()),
                currencyMapper.mapFrom(object.getTargetCurrency()),
                object.getRate()
        );
    }
}
