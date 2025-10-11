package com.example.study.member.command.application;

import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberChangePasswordService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(UUID memberId, String oldPassword, String newPassword) {

        Member member = memberRepository.findById(memberId)
                .filter(m -> passwordEncoder.isMatch(oldPassword, m.getPassword()))
                .orElseThrow(InvalidCredentialsException::new);

        if (oldPassword.equals(newPassword)) {
            throw new SameAsPreviousPasswordException();
        }

        // 새 비밀번호 암호화 후 변경
        member.changePassword(passwordEncoder.hashPassword(newPassword));
    }
}
