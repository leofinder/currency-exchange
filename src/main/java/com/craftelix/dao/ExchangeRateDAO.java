package com.craftelix.dao;

import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateDao extends Dao<ExchangeRate> {

    Optional<ExchangeRate> update(Currency base, Currency target, BigDecimal rate);

    Optional<ExchangeRate> findByCurrencies(Currency base, Currency target);
}
