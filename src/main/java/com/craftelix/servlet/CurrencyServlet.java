package com.craftelix.servlet;

import com.craftelix.dto.CurrencyResponseDto;
import com.craftelix.service.CurrencyService;
import com.craftelix.util.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        validateGetParameters(req);

        String code = req.getPathInfo().replaceAll("/", "").toUpperCase();

        CurrencyResponseDto currencyResponseDto = currencyService.findByCode(code);
        mapper.writeValue(resp.getWriter(), currencyResponseDto);
    }

    private void validateGetParameters(HttpServletRequest req) {
        String code  = req.getPathInfo().replaceAll("/", "");

        ValidationUtil.validateQueryParameter("Код валюты", code);
        ValidationUtil.validateLength("Код валюты", code, 3);
    }
}
