package com.example.study.unit;

import com.example.study.common.authentication.JwtConfig;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.common.authentication.fo.AuthenticationConstant;
import com.example.study.common.authentication.fo.TokenManager;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.ui.MemberLoginController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(MemberLoginController.class)
@Import(JwtConfig.class)
class MemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberLoginService memberLoginService;

    @MockitoBean
    private TokenManager tokenManager;

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
}
