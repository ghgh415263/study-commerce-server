package com.example.study.order.order.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 취소된 주문 ID (FK)
     */
    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderCancelReason reason;

    @Column(columnDefinition = "TEXT")
    private String detailReason;

    @Column(nullable = false)
    private LocalDateTime canceledAt;

    public OrderCancel(Long orderId, String reasonCode, String detailReason) {
        this.orderId = orderId;
        this.reason = OrderCancelReason.valueOf(reasonCode);
        this.detailReason = detailReason;
        this.canceledAt = LocalDateTime.now();
    }
}
