package com.example.study.member.command.application;

import com.example.study.member.command.domain.PasswordEncoder;
import com.example.study.member.command.domain.backofficemember.BackofficeMember;
import com.example.study.member.command.domain.backofficemember.BackofficeMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackofficeMemberLoginService {

    private final BackofficeMemberRepository backofficeMemberRepository;

    private final PasswordEncoder passwordEncoder;

    public UUID login(BackofficeMemberLoginDto backofficeMemberLoginDto) {
        return backofficeMemberRepository.findByLoginId(backofficeMemberLoginDto.loginId())
                .filter(member -> passwordEncoder.isMatch(backofficeMemberLoginDto.password(), member.getPassword()))
                .map(BackofficeMember::getId)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
