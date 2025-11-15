package com.example.study.unit;

import com.example.study.common.authentication.JwtConfig;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import com.example.study.member.test.TestBackofficeMemberLoginController;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestBackofficeMemberLoginController.class)
@Import(JwtConfig.class)
public class BackofficeMemberLoginControllerTest {

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

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    @DisplayName("DefaultMessageCodesResolver object 규칙을 테스트 한다.")
    void defaultMessageCodesResolverRules_Object() {
        // given
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "testObject");
        // then
        assertThat(messageCodes).containsExactly(
                "required.testObject"
                ,"required");
    }

    @Test
    @DisplayName("DefaultMessageCodesResolver field 규칙을 테스트 한다.")
    void defaultMessageCodesResolverRules_Field() {
        // given
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "testObject", "testObjectName", String.class);
        // then
        assertThat(messageCodes).containsExactly(
                "required.testObject.testObjectName"
                ,"required.testObjectName"
                ,"required.java.lang.String"
                ,"required"
        );
    }

    // Test for beanValidation_NotBlank
    @Getter
    @Setter
    static class TestLoginForm {
        @NotBlank(message = "loginId는 필수입니다.")
        private String loginId;

        @NotBlank(message = "password는 필수입니다.")
        private String password;
    }

    @Test
    @DisplayName("Bean Validation NotBlank 규칙을 테스트 한다.")
    void beanValidation_NotBlank() {
        // given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        TestLoginForm login = new TestLoginForm();
        login.setLoginId("  ");
        login.setPassword("");

        // when
        Set<ConstraintViolation<TestLoginForm>> violations = validator.validate(login);

        // then
        assertThat(violations.stream().map(ConstraintViolation::getMessage).toList()).containsExactlyInAnyOrder(
                "loginId는 필수입니다.", "password는 필수입니다."
        );
    }
}