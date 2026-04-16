package com.example.study.unit;

import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.common.authentication.fo.AuthenticationConfig;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.member.command.application.BackofficeMemberLoginDto;
import com.example.study.member.command.application.BackofficeMemberLoginSaveForm;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import com.example.study.member.command.application.InvalidCredentialsException;
import com.example.study.member.ui.backoffice.BackofficeMemberLoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BackofficeMemberLoginController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthenticationConfig.class
        )
)
public class BackofficeMemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackofficeMemberLoginService backofficeMemberLoginService;

    @MockitoBean
    private BackofficeTokenManager backofficeTokenManager;

    @Test
    @DisplayName("로그인 폼을 받는다.")
    void getloginView() throws Exception {
        mockMvc.perform(get("/backoffice/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form")))
                .andExpect(content().string(containsString("name=\"loginId\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공시 backOffice jwt가 발급되고 home 화면으로 redirect 한다.")
    void loginSuccessful_home_redirect() throws Exception {
        mockMvc.perform(post("/backoffice/login")
                .param("loginId", "master")
                .param("password", "masterPassword123!"))
                /* redirect 검증 */
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backoffice/home"))
                /*  토큰 검증 */
                .andExpect(cookie().exists("BACKOFFICE_AUTH_TOKEN"))
                .andExpect(cookie().httpOnly("BACKOFFICE_AUTH_TOKEN", true))
                .andExpect(cookie().secure("BACKOFFICE_AUTH_TOKEN", true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")))
                .andExpect(cookie().path("BACKOFFICE_AUTH_TOKEN", "/"))
                .andExpect(cookie().maxAge("BACKOFFICE_AUTH_TOKEN", 3600));
    }

    @Test
    @DisplayName("잘못된 아이디 패스워드로 로그인시 실패시 에러 메세지와 함께 home 화면으로 redirect 한다.")
    void loginFailRedirectHome() throws Exception {
        // given
        BackofficeMemberLoginSaveForm saveForm = new BackofficeMemberLoginSaveForm("testId", "testPassword");
        BackofficeMemberLoginDto loginDto = new BackofficeMemberLoginDto(saveForm.getLoginId(),saveForm.getPassword());

        // when
        doThrow(new InvalidCredentialsException())
                .when(backofficeMemberLoginService)
                .login(loginDto);

        // then
        mockMvc.perform(post("/backoffice/login")
                .param("loginId", "testId")
                .param("password", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backoffice/login"))
                .andExpect(flash().attributeExists("login"))
                .andExpect(flash().attribute("login",
                        hasProperty("loginId", equalTo(saveForm.getLoginId()))
                ))
                .andExpect(flash().attribute("login",
                        hasProperty("password", equalTo(saveForm.getPassword()))
                ))
                .andExpect(flash().attribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 패스워드 공백일 경우 BindingResult를 반환한다.")
    void loginValidation() throws Exception {
        // given
        BackofficeMemberLoginSaveForm saveForm = new BackofficeMemberLoginSaveForm("", "");

        // then
        mockMvc.perform(post("/backoffice/login")
                        .param("loginId", saveForm.getLoginId())
                        .param("password", saveForm.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("backoffice/login"))
                .andExpect(model().attributeExists("login"))
                .andExpect(model().attributeHasFieldErrors("login", "loginId"))
                .andExpect(model().attributeHasFieldErrors("login", "password"));
    }
}