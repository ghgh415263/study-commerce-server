package com.example.study.common.authentication.fo;

import com.example.study.common.util.AuthenticationUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class TokenManager {

    private final KeyPair keyPair;

    private final JwtBlacklistRepository jwtBlacklistRepository;

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

        Claims claims = extractClaimsFromToken(jwtToken);

        jwtBlacklistRepository.findByJwtHashId(AuthenticationUtils.hashJwtWithSHA256(jwtToken))
                .ifPresent(blacklist -> {throw new AuthenticationNotValidException();});

        Long memberId =  claims.get("id", Long.class);

        return new Authentication(memberId);
    }

    public void expireToken(String token) {
        Claims claims = extractClaimsFromToken(token);
        Date exp = claims.getExpiration();
        JwtBlacklist blacklist = new JwtBlacklist(token, claims.get("id", Long.class), exp.toInstant(), LocalDateTime.now());
        jwtBlacklistRepository.save(blacklist);
    }

    /**
     * 토큰으로부터 Claims을 조회한다. 만료된(expired) 토큰일 경우 예외를 던진다.
     * @param jwtToken
     * @return
     */
    private Claims extractClaimsFromToken(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

        } catch (JwtException e) {  // JJWT 모든 예외의 상위 타입
            throw new AuthenticationNotValidException("유효하지 않은 토큰입니다.", e);
        }
    }
}
