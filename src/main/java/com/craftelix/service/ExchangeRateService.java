package com.craftelix.service;

import com.craftelix.dao.ExchangeRateDao;
import com.craftelix.dao.JdbcExchangeRateDao;
import com.craftelix.dto.CreateExchangeRateDto;
import com.craftelix.dto.ExchangeRateDto;
import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.mapper.CreateExchangeRateMapper;
import com.craftelix.mapper.ExchangeRateMapper;
import com.craftelix.mapper.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ExchangeRateDao exchangeRateDao = JdbcExchangeRateDao.getInstance();

    private final Mapper<CreateExchangeRateDto, ExchangeRate> createExchangeRateMapper = CreateExchangeRateMapper.getInstance();
    private final Mapper<ExchangeRate, ExchangeRateDto> exchangeRateMapper = ExchangeRateMapper.getInstance();

    private ExchangeRateService() {

    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRateMapper::mapFrom)
                .collect(toList());
    }

    public ExchangeRateDto save(CreateExchangeRateDto createExchangeRateDto) {
        ExchangeRate createExchangeRate = createExchangeRateMapper.mapFrom(createExchangeRateDto);
        ExchangeRate exchangeRate = exchangeRateDao.save(createExchangeRate);
        return exchangeRateMapper.mapFrom(exchangeRate);
    }

    public ExchangeRateDto findByCurrencies(String baseCode, String targetCode) {
        Currency base = currencyService.findCurrencyByCode(baseCode);
        Currency target = currencyService.findCurrencyByCode(targetCode);

        Optional<ExchangeRate> optionalExchangeRate = exchangeRateDao.findByCurrencies(base, target);
        if (optionalExchangeRate.isPresent()) {
            return exchangeRateMapper.mapFrom(optionalExchangeRate.get());
        } else {
            throw new DataNotFoundException("Обменный курс для пары %s-%s не найден".formatted(baseCode, targetCode));
        }
    }

    public ExchangeRateDto update(String baseCode, String targetCode, BigDecimal rate) {
        Currency base = currencyService.findCurrencyByCode(baseCode);
        Currency target = currencyService.findCurrencyByCode(targetCode);

        Optional<ExchangeRate> optionalExchangeRate = exchangeRateDao.update(base, target, rate);
        if (optionalExchangeRate.isPresent()) {
            return exchangeRateMapper.mapFrom(optionalExchangeRate.get());
        } else {
            throw new DataNotFoundException("Валютная пара %s-%s отсутствует в базе данных".formatted(baseCode, targetCode));
        }

    }
}
