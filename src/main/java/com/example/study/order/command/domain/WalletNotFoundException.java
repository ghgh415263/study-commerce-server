package com.example.study.order.command.domain;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class WalletNotFoundException extends CustomException {

    public WalletNotFoundException(UUID memberId) {
        super("월렛을 찾을 수 없습니다. 회원 ID: " + memberId, HttpStatus.NOT_FOUND);
    }

    public WalletNotFoundException() {
        super("월렛을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
