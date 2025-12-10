package com.example.study.product.command.application.review;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidImageFileException extends CustomException {
    public InvalidImageFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
