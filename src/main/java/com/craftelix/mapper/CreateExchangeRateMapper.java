package com.craftelix.mapper;

import com.craftelix.dto.CreateExchangeRateDto;
import com.craftelix.entity.ExchangeRate;
import com.craftelix.service.CurrencyService;

public class CreateExchangeRateMapper implements Mapper<CreateExchangeRateDto, ExchangeRate> {

    private static final CreateExchangeRateMapper INSTANCE = new CreateExchangeRateMapper();

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private CreateExchangeRateMapper() {

    }

    public static CreateExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ExchangeRate mapFrom(CreateExchangeRateDto object) {
        return new ExchangeRate(
                currencyService.findCurrencyByCode(object.baseCurrencyCode()),
                currencyService.findCurrencyByCode(object.targetCurrencyCode()),
                object.rate()
        );
    }
}
