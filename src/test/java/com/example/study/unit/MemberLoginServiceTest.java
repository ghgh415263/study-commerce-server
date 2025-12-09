package com.example.study.unit;

import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.application.InvalidCredentialsException;
import com.example.study.member.command.application.MemberLoginDto;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import com.example.study.unit.help.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberLoginServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private MemberLoginService memberLoginService;

    @Test
    @DisplayName("로그인 성공")
    void login_successful() {
        // given
        MemberLoginDto dto = new MemberLoginDto("testLoginId", "rawPassword");

        Member member = MemberTestFactory.createMember("testLoginId", "encodedPassword", 1L);

        Mockito.when(memberRepository.findByLoginId("testLoginId"))
                .thenReturn(Optional.of(member));

        Mockito.when(passwordEncoder.isMatch("rawPassword", "encodedPassword"))
                .thenReturn(true);

        Mockito.when(tokenManager.generateToken(1L))
                .thenReturn("jwt-token");

        // when
        String token = memberLoginService.login(dto);

        // then
        assertEquals("jwt-token", token);
    }

    @Test
    @DisplayName("로그인 실패, 비밀번호 불일치")
    void login_fail_incorrect_password() {
        // given
        MemberLoginDto dto = new MemberLoginDto("testLoginId", "wrongPassword");

        Member member = MemberTestFactory.createMember("testLoginId", "encodedPassword", 1L);

        Mockito.when(memberRepository.findByLoginId("testLoginId"))
                .thenReturn(Optional.of(member));

        Mockito.when(passwordEncoder.isMatch("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> memberLoginService.login(dto));
    }

    @Test
    @DisplayName("로그인 실패 회원없음")
    void login_fail_not_found() {
        // given
        MemberLoginDto dto = new MemberLoginDto("unknown", "password");

        Mockito.when(memberRepository.findByLoginId("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> memberLoginService.login(dto));
    }
}
