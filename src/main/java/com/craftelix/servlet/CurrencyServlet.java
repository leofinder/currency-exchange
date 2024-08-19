package com.craftelix.servlet;

import com.craftelix.dto.CurrencyDto;
import com.craftelix.dto.ErrorMessageDto;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.exception.InvalidInputException;
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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            validateGetParameters(req);

            String requestURI = req.getRequestURI();
            String code = requestURI.replace("/currency/", "");

            CurrencyDto currencyDto = currencyService.findByCode(code);
            mapper.writeValue(resp.getWriter(), currencyDto);
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        } catch (DataNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        }
    }

    private void validateGetParameters(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        String code  = requestURI.replace("/currency/", "");

        ValidationUtil.validateHeadParameter("code", code);
        ValidationUtil.validateLength("code", code, 3);
    }
}
