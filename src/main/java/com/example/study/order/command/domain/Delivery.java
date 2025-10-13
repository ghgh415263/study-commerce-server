package com.example.study.order.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배송 상태 (예: READY, SHIPPED, DELIVERED, CANCELED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    // 배송 주소
    @Column(nullable = false, length = 255)
    private String address;

    // 배송 연락처 (전화번호)
    @Column(nullable = false, length = 20)
    private String contact;

    // 운송장 번호
    @Column(unique = true, length = 50)
    private String trackingNumber;

    // 배송 시작 일시
    private LocalDateTime shippedAt;

    // 배송 완료 일시
    private LocalDateTime deliveredAt;

    @Column(nullable = false)
    private Long orderId;

    public Delivery(DeliveryStatus status, String address, String contact, String trackingNumber) {
        this.status = status;
        this.address = address;
        this.contact = contact;
        this.trackingNumber = trackingNumber;
    }
}
