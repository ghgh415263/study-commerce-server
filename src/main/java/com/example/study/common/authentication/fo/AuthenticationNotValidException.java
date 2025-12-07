package com.example.study.common.authentication.fo;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 *
 */
public class AuthenticationNotValidException extends CustomException {
    public AuthenticationNotValidException() {
        super("인증에 실패하였습니다", HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationNotValidException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }
}
