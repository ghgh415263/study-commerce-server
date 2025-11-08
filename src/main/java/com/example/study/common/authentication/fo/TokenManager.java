package com.example.study.common.authentication.fo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.KeyPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class TokenManager {

    private final KeyPair keyPair;

    public String generateToken(Long id) {

        Instant now = Instant.now();
        Instant expiry = now.plus(30, ChronoUnit.MINUTES);

        return Jwts.builder()
                .claim("id", id)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public Authentication getAuthentication(String jwtToken){
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        Long memberId =  claims.get("id", Long.class);

        return new Authentication(memberId);
    }

}
