package com.craftelix.servlet;

import com.craftelix.dto.ExchangeRequestDto;
import com.craftelix.dto.ExchangeResponseDto;
import com.craftelix.dto.ErrorMessageDto;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.exception.InvalidInputException;
import com.craftelix.service.ExchangeService;
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

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        validateGetParameters(req);

        String baseCode = req.getParameter("from");
        String targetCode = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(baseCode, targetCode, amount);
        ExchangeResponseDto exchangeResponseDto = exchangeService.get(exchangeRequestDto);
        mapper.writeValue(resp.getWriter(), exchangeResponseDto);
    }

    private void validateGetParameters(HttpServletRequest req) {
        List<String> parameters = Arrays.asList("from", "to", "amount");
        for (String parameter : parameters) {
            ValidationUtil.validateQueryParameter(parameter, req.getParameter(parameter));
        }

        ValidationUtil.validateLength("from", req.getParameter("from"), 3);
        ValidationUtil.validateLength("to", req.getParameter("to"), 3);

        ValidationUtil.validateBigDecimalFormat("amount", req.getParameter("amount"));
    }
}
