package com.example.study.product.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Product 엔티티의 컬럼값이 음수일 경우 발생하는 예외입니다.
 */
public class InvalidProductColumnException extends CustomException {

    /**
     * @param column 입력한 엔티티 컬럼
     */
    public InvalidProductColumnException(String column) {
        super(String.format("엔티티 컬럼 %s 는 음수가 될 수 없습니다.", column),
                HttpStatus.BAD_REQUEST);
    }
}
