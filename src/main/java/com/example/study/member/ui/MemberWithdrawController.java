package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.HttpCookieManager;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.common.authentication.fo.AuthenticationConstant;
import com.example.study.common.authentication.fo.AuthenticationNotValidException;
import com.example.study.common.authentication.fo.UnauthenticatedException;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.application.MemberWithdrawService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberWithdrawController {

    private final MemberWithdrawService memberWithdrawService;

    private final HttpCookieManager httpCookieManager;

    @DeleteMapping
    public ApiSuccessResponse<Void> withdraw(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {

        String token = httpCookieManager.getCookieValue(request, AuthenticationConstant.TOKEN_COOKIE_NAME)
                .orElseThrow(UnauthenticatedException::new);
        // 1. 회원 탈퇴 처리
        memberWithdrawService.withdrawMember(authentication.getMemberId(), token);

        response.addHeader(HttpHeaders.SET_COOKIE, httpCookieManager.expireLoginCookie(AuthenticationConstant.TOKEN_COOKIE_NAME));

        return ApiSuccessResponse.empty();
    }
}
