package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.HttpCookieManager;
import com.example.study.common.authentication.fo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberLogoutController {

    private final TokenManager tokenManager;

    private final HttpCookieManager httpCookieManager;

    @PostMapping("/logout")
    public ApiSuccessResponse<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = httpCookieManager.getCookieValue(request, AuthenticationConstant.TOKEN_COOKIE_NAME)
                .orElseThrow(UnauthenticatedException::new);

        tokenManager.expireToken(token);

        response.addHeader(HttpHeaders.SET_COOKIE, httpCookieManager.expireLoginCookie(AuthenticationConstant.TOKEN_COOKIE_NAME));

        return ApiSuccessResponse.empty();
    }
}
