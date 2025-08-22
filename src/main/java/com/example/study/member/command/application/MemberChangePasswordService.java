package com.example.study.member.command.application;

import com.example.study.common.authentication.AuthenticationContext;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberChangePasswordService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {

        Member member = memberRepository.findById(authenticationContext.getAuthentication().getMemberId())
                .filter(m -> passwordEncoder.isMatch(oldPassword, m.getPassword()))
                .orElseThrow(InvalidCredentialsException::new);

        if (oldPassword.equals(newPassword)) {
            throw new SameAsPreviousPasswordException();
        }

        // 새 비밀번호 암호화 후 변경
        member.changePassword(passwordEncoder.hashPassword(newPassword));
    }
}
