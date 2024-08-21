package com.craftelix.mapper;

import com.craftelix.dto.CurrencyResponseDto;
import com.craftelix.entity.Currency;

public class CurrencyMapper implements Mapper<Currency, CurrencyResponseDto> {

    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    private CurrencyMapper() {

    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public CurrencyResponseDto mapFrom(Currency object) {
        return new CurrencyResponseDto(
                object.getId(),
                object.getCode(),
                object.getFullName(),
                object.getSign()
        );
    }
}
