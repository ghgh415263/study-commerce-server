package com.example.study.common.authentication.fo;

import com.example.study.common.authentication.backoffice.BackofficeAuthenticationArgumentResolver;
import com.example.study.common.authentication.backoffice.BackofficeLoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 인터셉터를 등록합니다.
 * - LoginInterceptor: 일반 사용자 로그인 확인용
 * - BackofficeLoginInterceptor: 관리자(백오피스) 로그인 확인용
 *
 * 각각 지정된 URL 경로 패턴에만 적용됩니다.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final BackofficeLoginInterceptor backofficeLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/members/**", "/orders/**", "/reviews/**", "/products/*/reviews", "/logout");

        registry.addInterceptor(backofficeLoginInterceptor)
                .addPathPatterns("/backoffice/**")
                .excludePathPatterns(
                        "/backoffice/login",
                        "/backoffice/api/login"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(List.of(
                new AuthenticationArgumentResolver(),
                new BackofficeAuthenticationArgumentResolver()
        ));
    }
}
