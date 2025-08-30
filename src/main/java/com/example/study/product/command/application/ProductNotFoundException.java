package com.example.study.product.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends CustomException{

    public ProductNotFoundException() {
        super("해당 상품정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
