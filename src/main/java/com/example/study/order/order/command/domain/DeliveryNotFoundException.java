package com.example.study.order.order.command.domain;

import org.springframework.http.HttpStatus;

import com.example.study.common.CustomException;

public class DeliveryNotFoundException extends CustomException {
    public DeliveryNotFoundException() {
        super("배송을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
