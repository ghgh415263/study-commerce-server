package com.example.study.common.authentication.backoffice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

/**
 * 인증된 백오피스 사용자만 접근 가능한 요청에 대해 세션 로그인 상태를 검사하고,
 * 로그인된 관리자 ID를 {@link BackofficeAuthenticationHolder}에 저장하는 인터셉터입니다.
 *
 * 요청 종료 시 {@code ThreadLocal}에 저장된 백오피스 사용자 정보를 정리합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class BackofficeLoginInterceptor implements HandlerInterceptor {

    private final BackofficeTokenManager backofficeTokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = extractTokenFromCookie(request);
        if (token == null) {
            try {
                response.sendRedirect("/backoffice/login");
            } catch (IOException e) {
                log.info("Failed to redirect to /backoffice/login due to missing token: {}", e.getMessage());
            }
            return false;
        }

        try {
            BackOfficeAuthentication authentication = backofficeTokenManager.getBackOfficeAuthentication(token);
            BackofficeAuthenticationHolder.set(authentication);
            return true;

        } catch (Exception e) {
            try {
                response.sendRedirect("/backoffice/login");
            } catch (IOException ioException) {
                log.info("Failed to redirect after authentication error: {}", ioException.getMessage());
            }
            return false;
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (BACKOFFICE_AUTHENTICATION.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        BackofficeAuthenticationHolder.clear();
    }
}
