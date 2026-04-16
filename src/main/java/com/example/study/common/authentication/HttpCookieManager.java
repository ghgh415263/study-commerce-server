package com.example.study.common.authentication;

import com.example.study.common.authentication.fo.AuthenticationConstant;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class HttpCookieManager {

    /**
     * 특정 쿠키 존재 여부 확인
     */
    public boolean hasCookie(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            return false;
        }

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookieName.equals(cookie.getName()));
    }

    /**
     * 특정 쿠키 값 가져오기 (없으면 Optional.empty())
     */
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * 특정 쿠키 값 가져오기 (없으면 null 반환)
     */
    public String getCookieValueOrNull(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName).orElse(null);
    }

    public String expireLoginCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }

    public String generateLoginCookie(String token) {
        return  ResponseCookie.from(AuthenticationConstant.TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build()
                .toString();
    }
}
