package com.craftelix.service;

import com.craftelix.dao.JdbcExchangeRateDao;
import com.craftelix.dto.ExchangeRequestDto;
import com.craftelix.dto.ExchangeResponseDto;
import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeEntity;
import com.craftelix.entity.ExchangeRate;
import com.craftelix.mapper.ExchangeResponseMapper;
import com.craftelix.mapper.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

    private static final ExchangeService INSTANCE = new ExchangeService();

    private final JdbcExchangeRateDao exchangeRateDao = JdbcExchangeRateDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private final Mapper<ExchangeEntity, ExchangeResponseDto> exchangeResponseMapper = ExchangeResponseMapper.getInstance();

    private ExchangeService() {

    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public ExchangeResponseDto get(ExchangeRequestDto exchangeRequestDto) {

        Currency base = currencyService.findCurrencyByCode(exchangeRequestDto.baseCurrencyCode());
        Currency target = currencyService.findCurrencyByCode(exchangeRequestDto.targetCurrencyCode());
        BigDecimal amount = exchangeRequestDto.amount();

        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCurrencies(base, target);
        if (exchangeRate.isPresent()) {
            BigDecimal rate = exchangeRate.get().getRate();
            ExchangeEntity exchangeEntity = buildExchangeEntity(base, target, rate, amount);
            return exchangeResponseMapper.mapFrom(exchangeEntity);
        }

        Optional<ExchangeRate> reverseExchangeRate = exchangeRateDao.findByCurrencies(target, base);
        if (reverseExchangeRate.isPresent()) {
            BigDecimal rate = new BigDecimal(1).divide(reverseExchangeRate.get().getRate(), 6, RoundingMode.HALF_UP);
            ExchangeEntity exchangeEntity = buildExchangeEntity(base, target, rate, amount);
            return exchangeResponseMapper.mapFrom(exchangeEntity);
        }

        Currency currencyUSD = currencyService.findCurrencyByCode("USD");
        Optional<ExchangeRate> usdBaseExchangeRate = exchangeRateDao.findByCurrencies(currencyUSD, base);
        Optional<ExchangeRate> usdTargetExchangeRate = exchangeRateDao.findByCurrencies(currencyUSD, target);

        if (usdBaseExchangeRate.isPresent() && usdTargetExchangeRate.isPresent()) {
            BigDecimal usdBaseRate = usdBaseExchangeRate.get().getRate();
            BigDecimal usdTargetRate = usdTargetExchangeRate.get().getRate();
            BigDecimal rate = usdBaseRate.divide(usdTargetRate, 6, RoundingMode.HALF_UP);
            ExchangeEntity exchangeEntity = buildExchangeEntity(base, target, rate, amount);
            return exchangeResponseMapper.mapFrom(exchangeEntity);
        } else {
            throw new RuntimeException();
        }
    }

    private static ExchangeEntity buildExchangeEntity(Currency base, Currency target, BigDecimal rate, BigDecimal amount) {
        return new ExchangeEntity(
                base,
                target,
                rate,
                amount,
                rate.multiply(amount)
        );
    }
}
