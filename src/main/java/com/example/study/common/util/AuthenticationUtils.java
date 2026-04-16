package com.example.study.common.util;

import com.example.study.common.authentication.fo.AuthenticationConstant;
import org.springframework.http.ResponseCookie;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

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

}
