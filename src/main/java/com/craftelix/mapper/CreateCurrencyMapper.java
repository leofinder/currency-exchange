package com.craftelix.mapper;

import com.craftelix.dto.CurrencyRequestDto;
import com.craftelix.entity.Currency;

public class CreateCurrencyMapper implements Mapper<CurrencyRequestDto, Currency> {

    private static final CreateCurrencyMapper INSTANCE = new CreateCurrencyMapper();

    private CreateCurrencyMapper() {

    }

    public static CreateCurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Currency mapFrom(CurrencyRequestDto object) {
        return new Currency(
                object.code(),
                object.name(),
                object.sign()
        );
    }
}
