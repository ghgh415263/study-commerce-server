package com.example.study.common.persistance;

import com.example.study.common.authentication.Authentication;
import com.example.study.common.authentication.AuthenticationConstant;
import com.example.study.common.authentication.BackOfficeAuthentication;
import com.example.study.common.authentication.BackoffIceAuthenticationConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * JPA 감사(Audit) 설정을 위한 Spring Configuration 클래스입니다.
 *
 * 현재 로그인한 사용자의 ID를 세션에서 가져와 감사자 정보로 사용하며,
 * 감사 시점의 현재 시간을 제공하는 DateTimeProvider도 함께 구현합니다.
 *
 * 세션이 없거나 userId가 없으면 "no_session" 문자열로 처리합니다.
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
     * 현재 요청의 세션에서 사용자 ID를 조회하여 감사자 정보를 제공하고,
     * 현재 시각을 제공하는 AuditorAware 및 DateTimeProvider 구현체입니다.
     */
    static class AuditorAwareImpl implements AuditorAware<String>, DateTimeProvider {

        private final static String NO_SESSION = "no_session";
        /**
         * 현재 감사자(userId)를 Optional로 반환합니다.
         * 세션 또는 userId가 없으면 "no_session"을 반환합니다.
         *
         * @return 현재 감사자 ID 또는 "no_session"
         */
        @Override
        public Optional<String> getCurrentAuditor() {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession session = request.getSession(false); // 세션 없으면 null 반환
            if (session == null) {
                return Optional.of(NO_SESSION);
            }
            var auth = (Authentication) session.getAttribute(AuthenticationConstant.AUTHENTICATION);
            if (auth != null) {
                return Optional.of(auth.getMemberId().toString());
            }
            var backofficeAuth = (BackOfficeAuthentication) session.getAttribute(BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION);
            if (backofficeAuth != null) {
                return Optional.of(backofficeAuth.getBackofficeMemberId().toString());
            }
            return Optional.of(NO_SESSION);
        }

        /**
         * 현재 시각을 ZonedDateTime 형태로 반환합니다.
         *
         * @return 현재 시간의 TemporalAccessor Optional
         */
        @Override
        public Optional<TemporalAccessor> getNow() {
            return Optional.of(ZonedDateTime.now());
        }
    }
}
