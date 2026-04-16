package com.example.study.common.authentication.fo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class FoLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Authentication authentication = AuthenticationHolder.get();

        if (authentication.isAnonymous())
            throw new UnauthenticatedException("로그인이 필요합니다");

        return true;
    }
}
