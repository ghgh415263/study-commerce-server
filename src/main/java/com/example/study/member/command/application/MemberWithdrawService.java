package com.example.study.member.command.application;

import com.example.study.common.authentication.fo.JwtBlacklist;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberRepository memberRepository;

    private final TokenManager tokenManager;

    /**
     * 회원 탈퇴 처리 메서드
     * 변경 감지가 정상적으로 반영되도록 먼저 flush를 실행하여 DB에 update 쿼리가 나가게 한 후,
     * 삭제 처리를 진행한다.
     * 이렇게 해야 누가 삭제했는지 aud 테이블에 정확히 기록된다.
     */
    @Transactional
    public void withdrawMember(Long memberId, String token) {

        tokenManager.expireToken(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.withdraw();

        memberRepository.saveAndFlush(member);

        memberRepository.delete(member);
    }
}
