package com.example.study.order.command.domain;

public enum CouponIssuanceStatus {
    READY,   // 발행 대기
    ISSUED,  // 발행 완료
    FAILED   // 발행 실패
}
