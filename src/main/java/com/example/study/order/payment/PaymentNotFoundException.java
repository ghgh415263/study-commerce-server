package com.example.study.order.payment;

import org.springframework.http.HttpStatus;

import com.example.study.common.CustomException;

public class PaymentNotFoundException extends CustomException {
    public PaymentNotFoundException() {
        super("지불정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
