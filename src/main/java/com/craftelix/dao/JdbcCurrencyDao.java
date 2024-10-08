package com.craftelix.dao;

import com.craftelix.entity.Currency;
import com.craftelix.exception.DatabaseOperationException;
import com.craftelix.exception.SQLConstraintsException;
import com.craftelix.util.ConnectionManager;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {

    private static final JdbcCurrencyDao INSTANCE = new JdbcCurrencyDao();

    private static final String SQLITE_CONSTRAINT_UNIQUE = "SQLITE_CONSTRAINT_UNIQUE";

    private JdbcCurrencyDao() {
    }

    public static JdbcCurrencyDao getInstance() {
        return INSTANCE;
    }

    public Currency save(Currency currency) {
        String sql = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt(1));
            }
            return currency;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLConstraintsException("Валюта с кодом %s уже существует".formatted(currency.getCode()));
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                if (SQLITE_CONSTRAINT_UNIQUE.equals(((SQLiteException) e).getResultCode().name())) {
                    throw new SQLConstraintsException("Валюта с кодом %s уже существует".formatted(currency.getCode()));
                }
            }
            throw new DatabaseOperationException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        String sql = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            WHERE code = ?
            """;
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            Currency currency = null;
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public List<Currency> findAll() {
        String sql = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            """;
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
