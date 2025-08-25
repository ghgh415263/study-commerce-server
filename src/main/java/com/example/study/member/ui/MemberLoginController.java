package com.example.study.member.ui;

import com.example.study.common.authentication.Authentication;
import com.example.study.common.authentication.AuthenticationConstant;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.common.ApiSuccessResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberLoginService memberLoginService;

    @PostMapping
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody MemberLoginDto dto,
            HttpSession session
    ) {
        UUID loginedMemberId = memberLoginService.login(dto);
		session.setAttribute(AuthenticationConstant.AUTHENTICATION, new Authentication(loginedMemberId));

        return ApiSuccessResponse.empty();
    }
}