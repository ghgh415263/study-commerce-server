package com.example.study.unit;

import com.example.study.common.authentication.Authentication;
import com.example.study.common.authentication.AuthenticationConstant;
import com.example.study.member.command.application.MemberLoginService;
import com.example.study.member.ui.MemberLoginController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(MemberLoginController.class)
class MemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberLoginService memberLoginService;

    @Test
    @DisplayName("로그인 성공해서 세션에 Authentication가 저장되고 응답은 200")
    void loginSuccessful_storesIdInSession_ReturnsSuccess() throws Exception {
        // given
        UUID mockId = UUID.randomUUID();
        given(memberLoginService.login(any())).willReturn(mockId);

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
                .andReturn();

        // then: 세션에 loginId 저장되었는지 확인
        HttpSession session = result.getRequest().getSession(false);
        assertThat(session).isNotNull();
		Authentication authentication = (Authentication) session.getAttribute(AuthenticationConstant.AUTHENTICATION);
        assertThat(authentication.getMemberId()).isEqualTo(mockId);
    }
}
