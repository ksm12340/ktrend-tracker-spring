package com.ktrend.ktrendtracker.sec;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class LoginIdValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if ("/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String loginId = request.getParameter("loginId");

            if (loginId == null || loginId.isBlank()) {
                redirectWithError(response, "아이디를 입력해주세요.");
                return;
            }

            if (!loginId.equals(loginId.trim())) {
                redirectWithError(response, "아이디 앞뒤에는 공백을 사용할 수 없습니다.");
                return;
            }

            if (loginId.contains(" ")) {
                redirectWithError(response, "아이디에는 공백을 사용할 수 없습니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect("/login?errorMessage=" + encodedMessage);
    }
}