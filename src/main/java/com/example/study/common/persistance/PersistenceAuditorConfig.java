package com.example.study.common.persistance;

import com.example.study.common.authentication.backoffice.BackofficeAuthenticationHolder;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.common.authentication.backoffice.BackOfficeAuthentication;
import com.example.study.common.authentication.fo.AuthenticationHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA 감사(Audit) 설정을 위한 Spring Configuration 클래스입니다.
 */
@EnableJpaAuditing
@Configuration
public class PersistenceAuditorConfig {

    /**
     * 현재 감사자를 제공하는 AuditorAware 빈을 등록합니다.
     *
     * @return 사용자 ID를 반환하는 AuditorAware 구현체
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    /**
     * 현재 요청의 세션에서 사용자 ID를 조회하여 감사자 정보를 제공
     */
    static class AuditorAwareImpl implements AuditorAware<String> {

        /**
         * 현재 감사자(MemberBaseId)를 Optional로 반환합니다.
         * 만약 비로그인 사용자라면 "NO_AUTH"을 반환합니다.
         *
         * @return 현재 MemberBaseId 또는 "NO_AUTH"
         */
        @Override
        public Optional<String> getCurrentAuditor() {

            Authentication authentication = AuthenticationHolder.get();
            if (authentication != null) {
                return Optional.of(authentication.getMemberId().toString());
            }

            BackOfficeAuthentication backofficeAuth = BackofficeAuthenticationHolder.get();
            if (backofficeAuth != null) {
                return Optional.of(backofficeAuth.getBackofficeMemberId().toString());
            }

            return Optional.of("NO_AUTH");
        }
    }
}
