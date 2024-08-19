package com.craftelix.service;

import com.craftelix.dao.CurrencyDao;
import com.craftelix.dao.JdbcCurrencyDao;
import com.craftelix.dto.CreateCurrencyDto;
import com.craftelix.dto.CurrencyDto;
import com.craftelix.entity.Currency;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.mapper.CreateCurrencyMapper;
import com.craftelix.mapper.CurrencyMapper;
import com.craftelix.mapper.Mapper;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = JdbcCurrencyDao.getInstance();

    private final Mapper<CreateCurrencyDto, Currency> createCurrencyMapper = CreateCurrencyMapper.getInstance();
    private final Mapper<Currency, CurrencyDto> currencyMapper = CurrencyMapper.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currencyMapper::mapFrom)
                .collect(toList());
    }

    public CurrencyDto save(CreateCurrencyDto createCurrencyDto) {
        Currency createCurrency = createCurrencyMapper.mapFrom(createCurrencyDto);
        Currency currency = currencyDao.save(createCurrency);
        return currencyMapper.mapFrom(currency);
    }

    public CurrencyDto findByCode(String code) {
        Currency currency = findCurrencyByCode(code);
        return currencyMapper.mapFrom(currency);
    }

    public Currency findCurrencyByCode(String code) {
        Optional<Currency> optionalCurrency = currencyDao.findByCode(code);
        if (optionalCurrency.isPresent()) {
            return optionalCurrency.get();
        } else {
            throw new DataNotFoundException("Валюта с кодом %s не найдена".formatted(code));
        }
    }
}
