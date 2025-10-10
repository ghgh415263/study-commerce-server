package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.AuthenticationContext;
import com.example.study.member.command.application.MemberChangePasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberChangePasswordController {

    private final MemberChangePasswordService memberChangePasswordService;
    private final AuthenticationContext authenticationContext;

    @PatchMapping("/password")
    public ApiSuccessResponse<Void> changePassword(
            @Valid @RequestBody MemberChangePasswordDto dto
    ) {
        UUID memberId = authenticationContext.getAuthentication().getMemberId();
        memberChangePasswordService.changePassword(memberId, dto.oldPassword(), dto.newPassword());
        return ApiSuccessResponse.empty();
    }
}