package com.example.study.common.event;

import com.example.study.common.CustomException;
import org.springframework.http.HttpStatus;

/**
 * Outbox 이벤트가 이미 최종 상태(SUCCESS/FAIL)인 경우 발생하는 예외
 */
public class OutboxEventAlreadyFinalizedException extends CustomException {

    public OutboxEventAlreadyFinalizedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}