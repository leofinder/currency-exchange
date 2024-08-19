package com.craftelix.mapper;

import com.craftelix.dto.CurrencyDto;
import com.craftelix.dto.ExchangeResponseDto;
import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeEntity;

public class ExchangeResponseMapper implements Mapper<ExchangeEntity, ExchangeResponseDto> {

    private static final ExchangeResponseMapper INSTANCE = new ExchangeResponseMapper();

    private final Mapper<Currency, CurrencyDto> currencyMapper = CurrencyMapper.getInstance();

    private ExchangeResponseMapper() {

    }

    public static ExchangeResponseMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ExchangeResponseDto mapFrom(ExchangeEntity object) {
        return new ExchangeResponseDto(
                currencyMapper.mapFrom(object.getBaseCurrency()),
                currencyMapper.mapFrom(object.getTargetCurrency()),
                object.getRate(),
                object.getAmount(),
                object.getConvertedAmount()
        );
    }
}
