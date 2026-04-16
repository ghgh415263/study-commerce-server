package com.example.study.common.authentication.fo;

import com.example.study.common.authentication.HttpCookieManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenManager tokenManager;

    private final HttpCookieManager httpCookieManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (httpCookieManager.hasCookie(request, AuthenticationConstant.TOKEN_COOKIE_NAME)) {
            String token = httpCookieManager.getCookieValueOrNull(request, AuthenticationConstant.TOKEN_COOKIE_NAME);
            try {
                Authentication authentication = tokenManager.getAuthentication(token);
                AuthenticationHolder.set(authentication);
            }
            catch (AuthenticationNotValidException jwtException) {
                response.addHeader("Set-Cookie", httpCookieManager.expireLoginCookie(AuthenticationConstant.TOKEN_COOKIE_NAME));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } else {
            AuthenticationHolder.set(Authentication.createAnonymousAuthentication());
        }
        return true;
    }

    /**
     * 요청 처리가 완료된 후 호출되며, {@link AuthenticationHolder}의 사용자 정보를 정리합니다.
     * 쓰레드풀을 사용하는 경우, 이전 사용자 정보가 남아있는 것을 방지하기 위해 반드시 호출되어야 합니다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AuthenticationHolder.clear();
    }
}
