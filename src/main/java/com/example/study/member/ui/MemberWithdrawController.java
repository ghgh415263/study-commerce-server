package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.Authentication;
import com.example.study.member.command.application.MemberWithdrawService;
import jakarta.servlet.http.HttpSession;
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

    @DeleteMapping
    public ApiSuccessResponse<Void> withdraw(Authentication authentication, HttpSession session) {
        // 1. 회원 탈퇴 처리
        memberWithdrawService.withdrawMember(authentication.getMemberId());

        // 2. 세션 무효화 (로그아웃)
        session.invalidate();

        return ApiSuccessResponse.empty();
    }
}
