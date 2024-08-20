package com.craftelix.mapper;

import com.craftelix.dto.CreateCurrencyDto;
import com.craftelix.entity.Currency;

public class CreateCurrencyMapper implements Mapper<CreateCurrencyDto, Currency> {

    private static final CreateCurrencyMapper INSTANCE = new CreateCurrencyMapper();

    private CreateCurrencyMapper() {

    }

    public static CreateCurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Currency mapFrom(CreateCurrencyDto object) {
        return new Currency(
                object.code(),
                object.name(),
                object.sign()
        );
    }
}
