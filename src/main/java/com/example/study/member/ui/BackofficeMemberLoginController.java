package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.BackOfficeAuthentication;
import com.example.study.common.authentication.BackoffIceAuthenticationConstant;
import com.example.study.member.command.application.BackofficeMemberLoginDto;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/backoffice/login")
@RequiredArgsConstructor
public class BackofficeMemberLoginController {

    private final BackofficeMemberLoginService backofficeMemberLoginService;

    @PostMapping
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody BackofficeMemberLoginDto dto,
            HttpSession session
    ) {
        UUID loginedMemberId = backofficeMemberLoginService.login(dto);
        session.setAttribute(BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION, new BackOfficeAuthentication(loginedMemberId));

        return ApiSuccessResponse.empty();
    }
}
