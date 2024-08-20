package com.craftelix.dto;

public record CurrencyDto(
        Integer id,
        String code,
        String name,
        String sign) {
}
