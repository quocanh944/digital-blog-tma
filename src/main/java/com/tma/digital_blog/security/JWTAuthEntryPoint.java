package com.tma.digital_blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTAuthEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private final ObjectMapper objectMapper;

    public JWTAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Set content type to application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Set status code to SC_UNAUTHORIZED
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a Map to represent the JSON payload
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("error", "Unauthorized");
        jsonResponse.put("message", authException.getMessage());

        // Use ObjectMapper to convert the Map to JSON and write it to the response
        PrintWriter out = response.getWriter();
        objectMapper.writeValue(out, jsonResponse);
        out.flush();
    }
}
