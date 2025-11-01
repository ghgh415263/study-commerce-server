package com.example.study.member.ui;

import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.common.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberLoginService memberLoginService;
    private final TokenManager tokenManager;

    @PostMapping
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody MemberLoginDto dto,
            HttpServletResponse response) {

        Long loginedMemberId = memberLoginService.login(dto);

        String token = tokenManager.generateToken(loginedMemberId);

        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ApiSuccessResponse.empty();
    }
}