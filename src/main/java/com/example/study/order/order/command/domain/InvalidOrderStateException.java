package com.example.study.order.order.command.domain;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidOrderStateException extends CustomException {
    protected InvalidOrderStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
