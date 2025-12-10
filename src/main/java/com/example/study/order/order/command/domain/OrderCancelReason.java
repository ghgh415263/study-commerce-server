package com.example.study.order.order.command.domain;

public enum OrderCancelReason {
    CHANGE_OF_MIND,     // 단순 변심
    WRONG_ORDER,        // 잘못 주문함
    OUT_OF_STOCK       // 재고 없음
}
