package com.example.study.learningtest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCodesResolverTest {

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
