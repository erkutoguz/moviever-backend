package com.erkutoguz.moviever_backend.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class UserAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\"errorMessage\":\"Restricted area!\", \"type\":\"unauthorized\"}");
        response.setStatus(403);
    }
}
