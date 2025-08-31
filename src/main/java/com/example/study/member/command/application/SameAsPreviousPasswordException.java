package com.example.study.member.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class SameAsPreviousPasswordException extends CustomException {

    private static final String DEFAULT_MESSAGE = "이전 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.";

    public SameAsPreviousPasswordException() {
        super(DEFAULT_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
