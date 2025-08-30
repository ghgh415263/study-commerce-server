package com.example.study.product.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 * Product 엔티티에 잘못된 값이 셋팅될려고 하면 발생하는 예외
 */
public class InvalidProductParameterException extends CustomException {

    /**
     * @param message
     */
    public InvalidProductParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
