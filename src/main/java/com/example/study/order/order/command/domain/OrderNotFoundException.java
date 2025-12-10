package com.example.study.order.order.command.domain;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends CustomException {
    public OrderNotFoundException() {
        super("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
