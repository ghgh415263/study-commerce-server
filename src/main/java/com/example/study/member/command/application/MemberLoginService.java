package com.example.study.member.command.application;

import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenManager tokenManager;

    public String login(MemberLoginDto memberLoginDto) {

        Member member = memberRepository.findByLoginId(memberLoginDto.loginId())
                .filter(m -> passwordEncoder.isMatch(memberLoginDto.password(), m.getPassword()))
                .orElseThrow(InvalidCredentialsException::new);

        return tokenManager.generateToken(member.getId());
    }
}
