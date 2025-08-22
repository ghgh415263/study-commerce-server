package com.example.study.unit;

import com.example.study.member.command.application.InvalidCredentialsException;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import com.example.study.unit.help.MemberTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MemberLoginServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private MemberLoginService memberLoginService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);

        // 해싱 없이 단순 비교만 하는 PasswordEncoder
        passwordEncoder = new PasswordEncoder() {
            @Override
            public String hashPassword(String rawPassword) {
                return rawPassword;
            }

            @Override
            public boolean isMatch(String rawPassword, String encodedPassword) {
                return rawPassword.equals(encodedPassword);
            }
        };

        memberLoginService = new MemberLoginService(memberRepository, passwordEncoder);
    }

    @Test
    void 로그인_성공() {
        // given
        Member member = MemberTestFactory.createMember("testUser", "1234" , UUID.randomUUID());

        when(memberRepository.findByLoginId("testUser"))
                .thenReturn(Optional.of(member));

        // when
        UUID result = memberLoginService.login(new MemberLoginDto("testUser", "1234"));

        // then
        assertThat(result).isEqualTo(member.getId());
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        Member member = new Member("testUser", "1234", "test@test.com", "홍길동", null);
        when(memberRepository.findByLoginId("testUser"))
                .thenReturn(Optional.of(member));

        assertThrows(InvalidCredentialsException.class,
                () -> memberLoginService.login(new MemberLoginDto("testUser", "wrongPw")));
    }

    @Test
    void 로그인_실패_아이디없음() {
        when(memberRepository.findByLoginId("noUser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> memberLoginService.login(new MemberLoginDto("noUser", "1234")));
    }
}