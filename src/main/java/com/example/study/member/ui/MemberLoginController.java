package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberLoginService memberLoginService;

    @PostMapping("/login")
    public ApiSuccessResponse<Void> login(
            @Valid @RequestBody MemberLoginDto dto,
            HttpServletResponse response) {

        String token = memberLoginService.login(dto);

        response.addHeader(HttpHeaders.SET_COOKIE, AuthenticationUtils.generateLoingCookie(token));

        return ApiSuccessResponse.empty();
    }

}