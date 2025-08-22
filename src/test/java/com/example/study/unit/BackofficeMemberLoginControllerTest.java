package com.example.study.unit;

import com.example.study.member.command.application.BackofficeMemberLoginService;
import com.example.study.member.ui.BackofficeMemberLoginController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BackofficeMemberLoginController.class)
public class BackofficeMemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackofficeMemberLoginService backofficeMemberLoginService;

    @Test
    @DisplayName("로그인 성공 시 세션에 backofficeLoginId가 저장되고 응답은 200")
    void loginSuccessful_storesIdInSession_ReturnsSuccess() throws Exception {
        // given
        UUID mockId = UUID.randomUUID();
        given(backofficeMemberLoginService.login(any())).willReturn(mockId);

        // when
        MvcResult result = mockMvc.perform(post("/backoffice/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "loginId": "master",
                                "password": "masterPassword123!"
                            }
                        """))
                .andExpect(status().isOk())
                .andReturn();

        // then: 세션에 backofficeLoginId 저장되었는지 확인
        HttpSession session = result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("backofficeLoginId")).isEqualTo(mockId.toString());
    }
}
