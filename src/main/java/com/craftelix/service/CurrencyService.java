package com.craftelix.service;

import com.craftelix.dao.CurrencyDao;
import com.craftelix.dao.JdbcCurrencyDao;
import com.craftelix.dto.CurrencyRequestDto;
import com.craftelix.dto.CurrencyResponseDto;
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

    private final Mapper<CurrencyRequestDto, Currency> createCurrencyMapper = CreateCurrencyMapper.getInstance();
    private final Mapper<Currency, CurrencyResponseDto> currencyMapper = CurrencyMapper.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyResponseDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currencyMapper::mapFrom)
                .collect(toList());
    }

    public CurrencyResponseDto save(CurrencyRequestDto currencyRequestDto) {
        Currency createCurrency = createCurrencyMapper.mapFrom(currencyRequestDto);
        Currency currency = currencyDao.save(createCurrency);
        return currencyMapper.mapFrom(currency);
    }

    public CurrencyResponseDto findByCode(String code) {
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
