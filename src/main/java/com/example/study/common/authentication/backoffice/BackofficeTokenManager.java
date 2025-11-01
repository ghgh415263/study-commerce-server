package com.example.study.common.authentication.backoffice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackofficeTokenManager {

    private final KeyPair keyPair;

    public String generateToken(Long id) {

        Instant now = Instant.now();
        Instant expiry = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .claim("id", id)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public BackOfficeAuthentication getBackOfficeAuthentication(String jwtToken){
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        Long id =  claims.get("id", Long.class);

        return new BackOfficeAuthentication(id);
    }

}