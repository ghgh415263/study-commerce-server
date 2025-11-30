package com.example.study.common.util;

import com.example.study.common.authentication.fo.AuthenticationConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Supplier;

/** jwt 인증 관련 유틸 함수 **/
public class AuthenticationUtils {

    /**
     * 객체 생성 방지
     */
    private AuthenticationUtils() {}

    /**
     * jwt를 hash 알고리즘을 통해 64 bit로 변환하는 함수
     * @param jwt
     * @return
     */
    public static String hashJwtWithSHA256(String jwt){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(jwt.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * 로그인 쿠키 생성
     * @param token
     * @return
     */
    public static String generateLoingCookie (String token){
        return  ResponseCookie.from(AuthenticationConstant.TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build()
                .toString();
    }

    /**
     * 로그인 쿠키 만료
     * @return
     */
    public static String expireLoginCookie(){
        return ResponseCookie.from(AuthenticationConstant.TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }

    /**
     * 쿠키로부터 COOKIE_NAME에 해당하는 token을 조회한다.
     * @param request
     * @param exceptionSupplier
     * @return
     */
    public static String extractTokenFromCookie(
            HttpServletRequest request
            , Supplier<? extends RuntimeException> exceptionSupplier
    ){
        return Arrays.stream(request.getCookies())
                .filter(c -> AuthenticationConstant.TOKEN_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(exceptionSupplier);
    }

    /**
     * 토큰으로부터 Claims을 조회한다.
     * @param keyPair
     * @param jwtToken
     * @return
     */
    public static Claims extractClaimsFromToken(KeyPair keyPair, String jwtToken){
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

}
