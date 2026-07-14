package com.ktrend.ktrendtracker.sec;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String loginId = request.getParameter("loginId");
        String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";

        if (loginId == null || loginId.isBlank()) {
            errorMessage = "아이디를 입력해주세요.";
        } else if (!loginId.equals(loginId.trim())) {
            errorMessage = "아이디 앞뒤에는 공백을 사용할 수 없습니다.";
        } else if (loginId.contains(" ")) {
            errorMessage = "아이디에는 공백을 사용할 수 없습니다.";
        }

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login?errorMessage=" + encodedMessage);
    }
}