package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.Authentication;
import com.example.study.member.command.application.MemberUpdateDto;
import com.example.study.member.command.application.MemberUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberUpdateController {

    private final MemberUpdateService memberUpdateService;

    @PatchMapping("/detail")
    public ApiSuccessResponse<Void> updateMember(
            @Valid @RequestBody MemberUpdateDto dto,
            Authentication authentication
    ) {
        memberUpdateService.updateMember(authentication.getMemberId(), dto);
        return ApiSuccessResponse.empty();
    }
}