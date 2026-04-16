package com.example.study.unit;

import com.example.study.common.authentication.JwtConfig;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.common.authentication.fo.AuthenticationConfig;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import com.example.study.member.test.TestBackofficeMemberLoginController;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TestBackofficeMemberLoginController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthenticationConfig.class
        )
)
public class TestBackofficeMemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackofficeMemberLoginService backofficeMemberLoginService;

    @MockitoBean
    private BackofficeTokenManager backofficeTokenManager;

    @Test
    @DisplayName("로그인 성공 시 토큰생성되고 응답은 200")
    void loginSuccessful_tokenCreated_ReturnsSuccess() throws Exception {
        // given
        long memberId = 1L;
        String token = "mocked-jwt-token";

        given(backofficeMemberLoginService.login(any())).willReturn(memberId);
        given(backofficeTokenManager.generateToken(memberId)).willReturn(token);

        // when
        MvcResult result = mockMvc.perform(post("/backoffice/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "loginId": "master",
                                "password": "masterPassword123!"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(cookie().value("BACKOFFICE_AUTH_TOKEN", token))
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("BACKOFFICE_AUTH_TOKEN");
        assertThat(tokenCookie).isNotNull();
        assertThat(tokenCookie.getValue()).isEqualTo(token);
    }
}