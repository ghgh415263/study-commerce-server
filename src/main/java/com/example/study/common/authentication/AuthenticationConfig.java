package com.example.study.common.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 인증 관련 인터셉터를 등록하는 설정 클래스입니다.
 */
@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {

    /**
     * 인터셉터를 등록합니다.
     * - LoginInterceptor: 일반 사용자 로그인 확인용
     * - BackofficeLoginInterceptor: 관리자(백오피스) 로그인 확인용
     *
     * 각각 지정된 URL 경로 패턴에만 적용됩니다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/members/**", "/orders/**");

        registry.addInterceptor(new BackofficeLoginInterceptor())
                .addPathPatterns("/backoffice/**")
                .excludePathPatterns("/backoffice/login");
    }
}
