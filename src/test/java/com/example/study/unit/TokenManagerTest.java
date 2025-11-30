package com.example.study.unit;

import com.example.study.common.authentication.fo.JwtBlacklistRepository;
import com.example.study.common.authentication.fo.TokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.study.unit.help.FixedKeyPairFactory;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenManagerTest {

    private TokenManager tokenManager;
    private KeyPair keyPair;
    private JwtBlacklistRepository jwtBlacklistRepository;

    @BeforeEach
    void setUp() {
        keyPair = FixedKeyPairFactory.loadFixedKeyPair(); // 항상 같은 RSA 키
        tokenManager = new TokenManager(keyPair, jwtBlacklistRepository);
    }

    @Test
    void generateToken_shouldContainCorrectClaims() {
        // given
        Long memberId = 123L;

        // when
        String token = tokenManager.generateToken(memberId);
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("id", Long.class)).isEqualTo(memberId);
    }
}
