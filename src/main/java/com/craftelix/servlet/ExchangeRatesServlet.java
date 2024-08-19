package com.craftelix.servlet;

import com.craftelix.dto.CreateExchangeRateDto;
import com.craftelix.dto.ExchangeRateDto;
import com.craftelix.error.ErrorMessage;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.exception.InvalidInputException;
import com.craftelix.service.ExchangeRateService;
import com.craftelix.util.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            mapper.writeValue(resp.getWriter(), exchangeRateService.findAll());
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorMessage(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            validatePostParameters(req);

            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            BigDecimal rate = new BigDecimal(req.getParameter("rate"));

            CreateExchangeRateDto createExchangeRateDto = new CreateExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDto exchangeRateDto = exchangeRateService.save(createExchangeRateDto);
            mapper.writeValue(resp.getWriter(), exchangeRateDto);
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ErrorMessage(e.getMessage()));
        } catch (DataNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new ErrorMessage(e.getMessage()));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorMessage(e.getMessage()));
        }
    }

    private void validatePostParameters(HttpServletRequest req) {
        List<String> parameters = Arrays.asList("baseCurrencyCode", "targetCurrencyCode", "rate");
        for (String parameter : parameters) {
            ValidationUtil.validateBodyParameter(parameter, req.getParameter(parameter));
        }

        ValidationUtil.validateLength("baseCurrencyCode", req.getParameter("baseCurrencyCode"), 3);
        ValidationUtil.validateLength("targetCurrencyCode", req.getParameter("targetCurrencyCode"), 3);

        ValidationUtil.validateBigDecimalFormat("rate", req.getParameter("rate"));
    }
}
