package com.craftelix.servlet;

import com.craftelix.dto.CreateCurrencyDto;
import com.craftelix.dto.CurrencyDto;
import com.craftelix.dto.ErrorMessageDto;
import com.craftelix.exception.InvalidInputException;
import com.craftelix.exception.SQLConstraintsException;
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
import java.util.Arrays;
import java.util.List;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mapper.writeValue(resp.getWriter(), currencyService.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        validatePostParameters(req);

        String code = req.getParameter("code").toUpperCase();
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        CreateCurrencyDto createCurrencyDto = new CreateCurrencyDto(code, name, sign);

        CurrencyDto currencyDto = currencyService.save(createCurrencyDto);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), currencyDto);
    }

    private void validatePostParameters(HttpServletRequest req) {
        List<String> parameters = Arrays.asList("code", "name", "sign");
        for (String parameter : parameters) {
            ValidationUtil.validateBodyParameter(parameter, req.getParameter(parameter));
        }
        ValidationUtil.validateLength("code", req.getParameter("code"), 3);
    }

}
