package com.example.study.product.command.application.review;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidReviewAuthenticationException extends CustomException {

    public InvalidReviewAuthenticationException() {
        super("자신이 작성한 리뷰만 수정 할 수 있습니다.", HttpStatus.NOT_FOUND);
    }
}
