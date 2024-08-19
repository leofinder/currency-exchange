package com.craftelix.mapper;

import com.craftelix.dto.CurrencyDto;
import com.craftelix.entity.Currency;

public class CurrencyMapper implements Mapper<Currency, CurrencyDto> {

    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    private CurrencyMapper() {

    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public CurrencyDto mapFrom(Currency object) {
        return new CurrencyDto(
                object.getId(),
                object.getCode(),
                object.getFullName(),
                object.getSign()
        );
    }
}
