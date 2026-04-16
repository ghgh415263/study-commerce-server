package com.example.study.common.authentication;

import com.example.study.common.authentication.backoffice.BackofficeLoginInterceptor;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.common.authentication.fo.AuthenticationInterceptor;
import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.common.authentication.fo.FoLoginInterceptor;
import com.example.study.common.authentication.fo.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    private final HttpCookieManager httpCookieManager;

    @Bean
    public KeyPair localKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Local용 RSA 키 생성 실패", e);
        }
    }

    @Bean
    public TokenManager tokenManager() {
        return new TokenManager(localKeyPair(), jwtBlacklistRepository);
    }

    @Bean
    public BackofficeTokenManager backofficeTokenManager() {
        return new BackofficeTokenManager(localKeyPair());
    }

    @Bean
    public FoLoginInterceptor foLoginInterceptor() {
        return new FoLoginInterceptor();
    }

    @Bean
    public BackofficeLoginInterceptor backofficeLoginInterceptor() {
        return new BackofficeLoginInterceptor(backofficeTokenManager(), httpCookieManager);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor(){
        return new AuthenticationInterceptor(tokenManager(), httpCookieManager);
    }
}
