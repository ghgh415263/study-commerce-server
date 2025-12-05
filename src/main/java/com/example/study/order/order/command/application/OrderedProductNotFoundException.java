package com.example.study.order.order.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class OrderedProductNotFoundException extends CustomException {
    public OrderedProductNotFoundException(List<Long> productIds) {
        super("해당 상품정보를 찾을 수 없습니다. productIds=" + productIds, HttpStatus.NOT_FOUND);
    }
}
