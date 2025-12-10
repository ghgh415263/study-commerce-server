package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.common.authentication.fo.AuthenticationNotValidException;
import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.common.util.AuthenticationUtils;
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

    @PostMapping("/logout")
    public ApiSuccessResponse<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = AuthenticationUtils.extractTokenFromCookie(request);

        tokenManager.expireToken(token);

        response.addHeader(HttpHeaders.SET_COOKIE, AuthenticationUtils.expireLoginCookie());

        return ApiSuccessResponse.empty();
    }
}
