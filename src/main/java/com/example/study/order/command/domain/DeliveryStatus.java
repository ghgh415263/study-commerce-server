package com.example.study.order.command.domain;

public enum DeliveryStatus {
    NOT_STARTED,
    READY,      // 배송 준비
    SHIPPED,    // 배송 중
    DELIVERED,  // 배송 완료
    CANCELED    // 배송 취소
}
