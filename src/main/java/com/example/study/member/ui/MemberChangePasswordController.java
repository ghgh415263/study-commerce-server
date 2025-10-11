package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.Authentication;
import com.example.study.member.command.application.MemberChangePasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberChangePasswordController {

    private final MemberChangePasswordService memberChangePasswordService;

    @PatchMapping("/password")
    public ApiSuccessResponse<Void> changePassword(
            @Valid @RequestBody MemberChangePasswordDto dto,
            Authentication authentication
    ) {
        memberChangePasswordService.changePassword(authentication.getMemberId(), dto.oldPassword(), dto.newPassword());
        return ApiSuccessResponse.empty();
    }
}