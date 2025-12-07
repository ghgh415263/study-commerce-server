package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.*;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.command.application.MemberNotFoundException;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberLoginService memberLoginService;
    private final TokenManager tokenManager;
    private final JwtBlacklistRepository jwtBlacklistRepository;

    @PostMapping("/login")
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody MemberLoginDto dto,
            HttpServletResponse response) {

        Long loginedMemberId = memberLoginService.login(dto);

        String token = tokenManager.generateToken(loginedMemberId);

        response.addHeader("Set-Cookie", AuthenticationUtils.generateLoingCookie(token));

        return ApiSuccessResponse.empty();
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response, Authentication authentication) {

        String token = AuthenticationUtils.extractTokenFromCookie(request, AuthenticationNotValidException::new);
        Instant expirationDateTime = tokenManager.getExpiration(token);

        JwtBlacklist blacklist = new JwtBlacklist(token, authentication.getMemberId(), expirationDateTime, LocalDateTime.now());

        jwtBlacklistRepository.save(blacklist);

        response.addHeader("Set-Cookie", AuthenticationUtils.expireLoginCookie());

        return ApiSuccessResponse.empty();
    }
}