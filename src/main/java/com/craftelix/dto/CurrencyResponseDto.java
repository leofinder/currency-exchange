package com.craftelix.dto;

public record CurrencyResponseDto(
        Integer id,
        String code,
        String name,
        String sign) {
}
