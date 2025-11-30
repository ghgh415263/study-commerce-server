package com.example.study.common.authentication.fo;

import com.example.study.common.util.AuthenticationUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

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

        validateBlacklistAndExpiration(jwtToken);

        Claims claims = AuthenticationUtils.extractClaimsFromToken(keyPair, jwtToken);

        Long memberId =  claims.get("id", Long.class);

        return new Authentication(memberId);
    }

    /** jwt 유효성 검증 **/
    private void validateBlacklistAndExpiration(String jwt) {

        Optional<JwtBlacklist> optional = jwtBlacklistRepository.findByJwtHashId(AuthenticationUtils.hashJwtWithSHA256(jwt));

        if (optional.isEmpty()) {
            return;
        }

        LocalDateTime logoutAt = optional.get().getLogoutAt();

        if (logoutAt.isBefore(LocalDateTime.now())) { // check expiredTime
            throw new JwtValidateTokenExpiration();
        }
    }
}
