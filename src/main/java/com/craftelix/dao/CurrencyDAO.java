package com.craftelix.dao;

import com.craftelix.entity.Currency;
import com.craftelix.exception.DaoException;
import com.craftelix.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {

    private static final CurrencyDAO INSTANCE = new CurrencyDAO();

    private static final String SAVE_SQL = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                code,
                full_name,
                sign
            FROM currencies
            """;
    private static final String FIND_BY_CODE = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    private CurrencyDAO() {
    }

    public static CurrencyDAO getInstance() {
        return INSTANCE;
    }

    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt("id"));
            }
            return currency;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE)) {
            statement.setString(1, code);
            Currency currency = null;
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DaoException(e);
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
