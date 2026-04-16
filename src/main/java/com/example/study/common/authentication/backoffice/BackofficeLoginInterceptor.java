package com.example.study.common.authentication.backoffice;

import com.example.study.common.authentication.HttpCookieManager;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

/**
 * 백오피스 관리자 전용 요청에 대해 인증 토큰을 검증하는 인터셉터입니다.
 *
 * 요청 쿠키에서 {@code BACKOFFICE_AUTHENTICATION} 토큰을 추출하여
 * 유효한 토큰일 경우 {@link BackofficeAuthenticationHolder} 에 인증 정보를 저장합니다.
 * 이를 통해 컨트롤러에서 인증된 관리자 정보를 조회할 수 있습니다.
 *
 * 토큰이 존재하지 않거나 검증에 실패한 경우 요청 처리를 중단하고
 * 백오피스 로그인 페이지({@code /backoffice/login})로 리다이렉트합니다.
 *
 * 요청이 정상 종료되거나 예외로 종료되는 경우,
 * {@code ThreadLocal} 에 저장된 인증 정보는 afterCompletion 단계에서 정리됩니다.
 *
 */
@Slf4j
@RequiredArgsConstructor
public class BackofficeLoginInterceptor implements HandlerInterceptor {

    private final BackofficeTokenManager backofficeTokenManager;

    private final HttpCookieManager httpCookieManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = httpCookieManager.getCookieValueOrNull(request, BACKOFFICE_AUTHENTICATION);

        if (token == null) {
            redirectToLoginPage(response);
            return false;
        }

        try {
            BackOfficeAuthentication authentication = backofficeTokenManager.getBackOfficeAuthentication(token);
            BackofficeAuthenticationHolder.set(authentication);
            return true;

        } catch (Exception e) {
            log.warn("백오피스 인증 실패={}", e.getMessage());
            redirectToLoginPage(response);
            return false;
        }
    }

    private void redirectToLoginPage(HttpServletResponse response) {
        try {
            response.sendRedirect("/backoffice/login");
        } catch (IOException e) {
            log.error("Failed to redirect to login page", e);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        BackofficeAuthenticationHolder.clear();
    }
}
