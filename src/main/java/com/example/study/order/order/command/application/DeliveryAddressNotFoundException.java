package com.example.study.order.order.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class DeliveryAddressNotFoundException extends CustomException {

    public DeliveryAddressNotFoundException() {
        super("해당 배송주소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}