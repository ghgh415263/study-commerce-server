package com.example.study.common.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인증된 백오피스 사용자만 접근 가능한 요청에 대해 세션 로그인 상태를 검사하고,
 * 로그인된 관리자 ID를 {@link BackofficeLoginMemberHolder}에 저장하는 인터셉터입니다.
 *
 * 요청 종료 시 {@code ThreadLocal}에 저장된 백오피스 사용자 정보를 정리합니다.
 */
public class BackofficeLoginInterceptor implements HandlerInterceptor {

    private static final String BACKOFFICE_SESSION_KEY = "backofficeLoginId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(BACKOFFICE_SESSION_KEY) == null) {
            throw new BackofficeUnauthenticatedException();
        }

        String adminId = (String) session.getAttribute(BACKOFFICE_SESSION_KEY);
        BackofficeLoginMemberHolder.set(adminId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        BackofficeLoginMemberHolder.clear();
    }
}
