package com.craftelix.dao;

import com.craftelix.entity.Currency;

import java.util.Optional;

public interface CurrencyDao extends Dao<Currency> {

    Optional<Currency> findByCode(String code);

}
