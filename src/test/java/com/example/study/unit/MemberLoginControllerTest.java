package com.example.study.unit;

import com.example.study.common.authentication.HttpCookieManager;
import com.example.study.common.authentication.JwtConfig;
import com.example.study.common.authentication.fo.AuthenticationConfig;
import com.example.study.common.authentication.fo.AuthenticationConstant;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.ui.MemberLoginController;
import com.example.study.member.ui.backoffice.BackofficeMemberLoginController;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(
        controllers = MemberLoginController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthenticationConfig.class
        )
)
class MemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberLoginService memberLoginService;

    @MockitoBean
    private TokenManager tokenManager;

    @MockitoBean
    private HttpCookieManager httpCookieManager;

    @Test
    @DisplayName("로그인 성공해서 세션에 Authentication가 저장되고 응답은 200")
    void loginSuccessful_tokenCreated_ReturnsSuccess() throws Exception {
        // given
        String token = "mocked-jwt-token";

        given(memberLoginService.login(any())).willReturn(token);

        given(httpCookieManager.generateLoginCookie(token))
                .willReturn(ResponseCookie.from(AuthenticationConstant.TOKEN_COOKIE_NAME, token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofMinutes(30))
                        .build()
                        .toString());

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
}
