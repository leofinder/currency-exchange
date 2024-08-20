package com.craftelix.filter;

import com.craftelix.dto.ErrorMessageDto;
import com.craftelix.exception.DataNotFoundException;
import com.craftelix.exception.InvalidInputException;
import com.craftelix.exception.SQLConstraintsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/*")
public class ExceptionFilter implements Filter {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidInputException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        } catch (DataNotFoundException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        } catch (SQLConstraintsException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        } catch (RuntimeException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorMessageDto(e.getMessage()));
        }
    }
}
