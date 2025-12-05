package com.example.study.order.couponproductcoupon.command;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 * 메시지 발행 실패
 */
public class EventPublishingFailedException extends CustomException {

    public EventPublishingFailedException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
