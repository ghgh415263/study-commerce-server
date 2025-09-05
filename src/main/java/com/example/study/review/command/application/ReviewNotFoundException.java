package com.example.study.review.command.application;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends CustomException {

    public ReviewNotFoundException() {
        super("해당 리뷰정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
