package com.example.study.common.authentication.fo;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 *
 */
public class JwtValidateTokenExpiration extends CustomException {
    public JwtValidateTokenExpiration() {
        super("만료된 사용자입니다. 재 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }

    public JwtValidateTokenExpiration(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
