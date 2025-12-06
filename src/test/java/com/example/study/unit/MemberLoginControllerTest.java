package com.example.study.unit;

import com.example.study.common.authentication.JwtConfig;
import com.example.study.common.authentication.fo.JwtBlacklist;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.ui.MemberLoginController;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberLoginController.class)
@Import(JwtConfig.class)
class MemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberLoginService memberLoginService;

    @MockitoBean
    private TokenManager tokenManager;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtBlacklistRepository jwtBlacklistRepository;

    @MockitoBean
    private KeyPair keyPair;

    @MockitoBean
    private PublicKey publicKey;

    @MockitoBean
    private PrivateKey privateKey;

    @Test
    @DisplayName("로그인 성공해서 세션에 Authentication가 저장되고 응답은 200")
    void loginSuccessful_tokenCreated_ReturnsSuccess() throws Exception {
        // given
        long memberId = 1L;
        String token = "mocked-jwt-token";

        given(memberLoginService.login(any())).willReturn(memberId);
        given(tokenManager.generateToken(memberId)).willReturn(token);

        // when
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "loginId": "user@example.com",
                                "password": "password123!H"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(cookie().value("AUTH_TOKEN", token)) // 쿠키에 토큰이 있는지 확인
                .andReturn();

        // then
        Cookie tokenCookie = result.getResponse().getCookie("AUTH_TOKEN");
        assertThat(tokenCookie).isNotNull();
        assertThat(tokenCookie.getValue()).isEqualTo(token);
    }

    @Test
    @DisplayName("로그아웃 시 AUTH_TOKEN 쿠키가 만료되고 JWT 블랙리스트가 저장된다.")
    void logoutSuccessful_tokenExpired_saveJwtBlacklist() throws Exception {
        try (MockedStatic<AuthenticationUtils> utilsMock = Mockito.mockStatic(AuthenticationUtils.class)) {
            /* given */
            long memberId = 1L;
            Member member = new Member("test", null, null, null, null);
            String token = "mocked-jwt-token";
            Cookie authCookie = new Cookie("AUTH_TOKEN", token);
            Claims claims = mock(Claims.class);
            Date expiration = new Date();

            // KeyPair 구성
            given(keyPair.getPublic()).willReturn(publicKey);
            given(keyPair.getPrivate()).willReturn(privateKey);

            // static mock - extractTokenFromCookie
            utilsMock.when(() ->
                    AuthenticationUtils.extractTokenFromCookie(
                            any(HttpServletRequest.class),
                            ArgumentMatchers.<Supplier<? extends RuntimeException>>any()
                    )
            ).thenReturn(token);

            // static mock - extractClaimsFromToken
            utilsMock.when(() ->
                    AuthenticationUtils.extractClaimsFromToken(
                            any(KeyPair.class),
                            anyString()
                    )
            ).thenReturn(claims);

            // claims mock
            given(claims.get("id", Long.class)).willReturn(memberId);
            given(claims.getExpiration()).willReturn(expiration);

            // repository mock
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // static mock - expireLoginCookie()
            utilsMock.when(AuthenticationUtils::expireLoginCookie)
                    .thenReturn("AUTH_TOKEN=; Max-Age=0; Path=/; HttpOnly");

            /* when & then */
            mockMvc.perform(post("/logout").cookie(authCookie))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Set-Cookie", containsString("AUTH_TOKEN=")))
                    .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));

            // verify blacklist
            ArgumentCaptor<JwtBlacklist> captor = ArgumentCaptor.forClass(JwtBlacklist.class);
            verify(jwtBlacklistRepository, times(1)).save(captor.capture());

            JwtBlacklist saved = captor.getValue();

            assertThat(saved.getMember()).isEqualTo(member);
            assertThat(saved.getExpiredAt()).isEqualTo(expiration);
            assertThat(saved.getLogoutAt()).isNotNull();
        }
    }
}
