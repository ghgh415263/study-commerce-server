package com.example.study.common;

import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends CustomException {

    public InvalidArgumentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}