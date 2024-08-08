package com.craftelix.dao;

import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;
import com.craftelix.exception.DaoException;
import com.craftelix.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO {

    private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();

    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            RETURNING id
            """;
    private static final String FIND_ALL_SQL = """
            SELECT rates.id,
                rates.rate,
                base.id base_id,
                base.code base_code,
                base.full_name base_full_name,
                base.sign base_sign,
                target.id target_id,
                target.code target_code,
                target.full_name target_full_name,
                target.sign target_sign
            FROM exchange_rates rates
            JOIN currencies base ON rates.base_currency_id = base.id
            JOIN currencies target ON rates.target_currency_id = target.id
            """;
    private static final String FIND_BY_CURRENCIES_SQL = FIND_ALL_SQL + """
            WHERE base.code = ? AND target.code = ?
            """;

    private ExchangeRateDAO() {
    }

    private static ExchangeRateDAO getInstance() {
        return INSTANCE;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, exchangeRate.getBaseCurrency().getId());
            statement.setInt(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt("id"));
            }
            return exchangeRate;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public ExchangeRate update(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManager.open();
        PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setInt(2, exchangeRate.getBaseCurrency().getId());
            statement.setInt(3, exchangeRate.getTargetCurrency().getId());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exchangeRate.setId(resultSet.getInt("id"));
            }
            return exchangeRate;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<ExchangeRate> findByCurrencies(Currency base, Currency target) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCIES_SQL)) {
            statement.setString(1, base.getCode());
            statement.setString(2, target.getCode());

            ResultSet resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRates(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRates(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private ExchangeRate buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                new Currency(
                        resultSet.getInt("base_id"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_full_name"),
                        resultSet.getString("base_sign")
                ),
                new Currency(
                        resultSet.getInt("target_id"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_full_name"),
                        resultSet.getString("target_sign")
                ),
                resultSet.getBigDecimal("rate")
        );
    }
}
