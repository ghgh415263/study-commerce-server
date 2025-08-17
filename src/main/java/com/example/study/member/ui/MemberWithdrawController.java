package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.LoginMemberContext;
import com.example.study.member.command.application.MemberWithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberWithdrawController {

    private final MemberWithdrawService memberWithdrawService;

    private final LoginMemberContext loginMemberContext;

    @DeleteMapping
    public ApiSuccessResponse<Void> withdraw() {
        memberWithdrawService.withdrawMember(UUID.fromString(loginMemberContext.getLoginId()));
        return ApiSuccessResponse.empty();
    }
}
