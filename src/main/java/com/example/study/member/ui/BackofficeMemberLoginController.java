package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.member.command.application.BackofficeMemberLoginDto;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

@RestController
@RequestMapping("/backoffice/login")
@RequiredArgsConstructor
public class BackofficeMemberLoginController {

    private final BackofficeMemberLoginService backofficeMemberLoginService;
    private final BackofficeTokenManager backofficeTokenManager;

    @PostMapping
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody BackofficeMemberLoginDto dto,
            HttpServletResponse response
    ) {
        Long loginedMemberId = backofficeMemberLoginService.login(dto);

        String token = backofficeTokenManager.generateToken(loginedMemberId);

        ResponseCookie cookie = ResponseCookie.from(BACKOFFICE_AUTHENTICATION, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ApiSuccessResponse.empty();
    }
}
