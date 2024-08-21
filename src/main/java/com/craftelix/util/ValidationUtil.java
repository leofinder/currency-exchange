package com.craftelix.util;

import com.craftelix.exception.InvalidInputException;

import java.math.BigDecimal;

public final class ValidationUtil {

    private ValidationUtil() {

    }

    public static void validateBodyParameter(String key, String value) {
        if (value == null) {
            throw new InvalidInputException("В теле запроса отсутствует параметр %s".formatted(key));
        } else if (value.isBlank()) {
            throw new InvalidInputException("В теле запроса не указано значение параметра %s".formatted(key));
        }
    }

    public static void validateQueryParameter(String key, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("В адресе отсутствует параметр %s".formatted(key));
        }
    }

    public static void validatePatchParameter(String parameter, String key) {
        if (parameter == null || !parameter.contains(key)) {
            throw new InvalidInputException("В теле запроса отсутствует параметр %s".formatted(key));
        }
        String value = parameter.replace("%s=".formatted(key), "");
        if (value.isBlank()) {
            throw new InvalidInputException("В теле запроса не указано значение параметра %s".formatted(key));
        }
    }

    public static void validateLength(String key, String value, int length) {
        if (value.length() != length) {
            throw new InvalidInputException("Длина параметра %s должна быть равна %d".formatted(key, length));
        }
    }

    public static void validateBigDecimalFormat(String key, String value) {
        try {
            BigDecimal number = new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Значение параметра %s не соответствует формату Decimal".formatted(key));
        }
    }
}
