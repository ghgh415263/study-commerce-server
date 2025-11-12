package com.example.study.member.test;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.member.command.application.BackofficeMemberLoginDto;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

@Tag(name = "Backoffice Test Login API", description = "백오피스 테스트 로그인 api")
@RestController
@RequestMapping("/backoffice/api/login")
@RequiredArgsConstructor
public class TestBackofficeMemberLoginController {

    private final BackofficeMemberLoginService backofficeMemberLoginService;
    private final BackofficeTokenManager backofficeTokenManager;
    @PostMapping
    public ApiSuccessResponse<Void> testLogin(
            @Valid @RequestBody TestBackofficeMemberLoginDto dto,
            HttpServletResponse response
    ) {
        BackofficeMemberLoginDto loginDto = new BackofficeMemberLoginDto(
                dto.loginId()
                , dto.password());

        Long loginedMemberId = backofficeMemberLoginService.login(loginDto);

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

