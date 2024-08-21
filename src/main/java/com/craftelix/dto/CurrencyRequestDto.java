package com.craftelix.dto;

public record CurrencyRequestDto(
        String code,
        String name,
        String sign) {
}
