package com.example.study.common.authentication;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 로그인되지 않은 사용자가 백오피스 회원 인증이 필요한 요청을 시도할 때 발생하는 예외입니다.
 * HTTP 상태 코드 401 (UNAUTHORIZED)을 반환합니다.
 */
public class BackofficeUnauthenticatedException extends CustomException {
    public BackofficeUnauthenticatedException() {
        super("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }
}
