package com.example.study.order.order.command.domain;

import org.springframework.http.HttpStatus;

import com.example.study.common.CustomException;

public class InvalidDeliveryStateException extends CustomException {
    protected InvalidDeliveryStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
