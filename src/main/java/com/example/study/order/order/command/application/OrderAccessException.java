package com.example.study.order.order.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class OrderAccessException extends CustomException {
    protected OrderAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
