package com.craftelix.servlet;

import com.craftelix.dto.ExchangeRateResponseDto;
import com.craftelix.service.ExchangeRateService;
import com.craftelix.util.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        validateGetParameters(req);

        String currenciesPair = req.getPathInfo().replaceAll("/", "").toUpperCase();

        String base = currenciesPair.substring(0, 3);
        String target = currenciesPair.substring(3);

        ExchangeRateResponseDto exchangeRateDto = exchangeRateService.findByCurrencies(base, target);
        mapper.writeValue(resp.getWriter(), exchangeRateDto);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try (BufferedReader reader = req.getReader()) {
            String parameter = reader.readLine();

            validatePatchParameters(req, parameter);

            String currenciesPair = req.getPathInfo().replaceAll("/", "").toUpperCase();

            String base = currenciesPair.substring(0, 3);
            String target = currenciesPair.substring(3);

            String paramRateValue = parameter.replace("rate=", "");
            BigDecimal rate = new BigDecimal(paramRateValue);

            ExchangeRateResponseDto exchangeRateDto = exchangeRateService.update(base, target, rate);
            mapper.writeValue(resp.getWriter(), exchangeRateDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateGetParameters(HttpServletRequest req) {
        String currenciesPair = req.getPathInfo().replaceAll("/", "");

        ValidationUtil.validateQueryParameter("Коды валют пары", currenciesPair);
        ValidationUtil.validateLength("Коды валют пары", currenciesPair, 6);
    }

    private void validatePatchParameters(HttpServletRequest req, String parameter) {
        String currenciesPair = req.getPathInfo().replaceAll("/", "");

        ValidationUtil.validateQueryParameter("Коды валют пары", currenciesPair);
        ValidationUtil.validateLength("Коды валют пары", currenciesPair, 6);

        ValidationUtil.validatePatchParameter(parameter, "rate");
        String paramRateValue = parameter.replace("rate=", "");
        ValidationUtil.validateBigDecimalFormat("rate", paramRateValue);
    }
}