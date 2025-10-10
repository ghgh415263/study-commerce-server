package com.example.study.member.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.AuthenticationContext;
import com.example.study.member.command.application.MemberUpdateDto;
import com.example.study.member.command.application.MemberUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberUpdateController {

    private final MemberUpdateService memberUpdateService;
    private final AuthenticationContext authenticationContext;

    @PatchMapping("/detail")
    public ApiSuccessResponse<Void> updateMember(
            @Valid @RequestBody MemberUpdateDto dto
    ) {
        UUID memberId = authenticationContext.getAuthentication().getMemberId();
        memberUpdateService.updateMember(memberId, dto);
        return ApiSuccessResponse.empty();
    }
}