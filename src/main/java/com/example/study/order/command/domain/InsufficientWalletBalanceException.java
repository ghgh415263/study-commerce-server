package com.example.study.order.command.domain;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InsufficientWalletBalanceException extends CustomException {
    public InsufficientWalletBalanceException(UUID memberId) {
        super("월렛 잔액이 부족합니다. 회원 ID: " + memberId, HttpStatus.BAD_REQUEST);
    }

    public InsufficientWalletBalanceException() {
        super("월렛 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);
    }
}