package com.craftelix.dao;

import com.craftelix.entity.Currency;
import com.craftelix.entity.ExchangeRate;
import com.craftelix.exception.DatabaseOperationException;
import com.craftelix.exception.SQLConstraintsException;
import com.craftelix.util.ConnectionManager;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {

    private static final JdbcExchangeRateDao INSTANCE = new JdbcExchangeRateDao();

    private static final String SQLITE_CONSTRAINT_UNIQUE = "SQLITE_CONSTRAINT_UNIQUE";

    private JdbcExchangeRateDao() {
    }

    public static JdbcExchangeRateDao getInstance() {
        return INSTANCE;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        String sql = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, exchangeRate.getBaseCurrency().getId());
            statement.setInt(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt(1));
            }
            return exchangeRate;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLConstraintsException("Валютная пара с кодом %s-%s уже существует"
                    .formatted(exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode()));
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                if (SQLITE_CONSTRAINT_UNIQUE.equals(((SQLiteException) e).getResultCode().name())) {
                    throw new SQLConstraintsException("Валютная пара с кодом %s-%s уже существует"
                            .formatted(exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode()));
                }
            }
            throw new DatabaseOperationException(e);
        }
    }

    public Optional<ExchangeRate> update(Currency base, Currency target, BigDecimal rate) {
        String sql = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            RETURNING id
            """;

        try (Connection connection = ConnectionManager.open();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, rate);
            statement.setInt(2, base.getId());
            statement.setInt(3, target.getId());

            ResultSet resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = new ExchangeRate(resultSet.getInt("id"), base, target, rate);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public Optional<ExchangeRate> findByCurrencies(Currency base, Currency target) {
        String sql = """
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
            WHERE base.code = ? AND target.code = ?
            """;

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, base.getCode());
            statement.setString(2, target.getCode());

            ResultSet resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRates(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public List<ExchangeRate> findAll() {
        String sql = """
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

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRates(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
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
