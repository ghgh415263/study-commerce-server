package com.example.study.order.order.command.domain;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class InsufficientWalletBalanceException extends CustomException {
    public InsufficientWalletBalanceException(Long memberId) {
        super("월렛 잔액이 부족합니다. 회원 ID: " + memberId, HttpStatus.BAD_REQUEST);
    }

    public InsufficientWalletBalanceException() {
        super("월렛 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);
    }
}