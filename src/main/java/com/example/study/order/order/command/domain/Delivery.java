package com.example.study.order.order.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Audited
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배송 상태
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

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    public Delivery(DeliveryStatus status, String address, String contact) {
        this.status = status;
        this.address = address;
        this.contact = contact;
    }

    public void addDeliveryItems(List<Long> orderItemIds) {
        for (Long orderItemId : orderItemIds) {
            DeliveryItem item = new DeliveryItem(orderItemId);
            item.setDelivery(this); // 양방향 연관관계 주입
            deliveryItems.add(item);
        }
    }

    public void ship(String trackingNumber) {
        if (this.status != DeliveryStatus.READY) {
            throw new IllegalStateException("배송 준비 상태에서만 출고 가능합니다.");
        }
        this.trackingNumber = trackingNumber;
        this.shippedAt = LocalDateTime.now();
        this.status = DeliveryStatus.SHIPPED;
    }

    public void complete() {
        if (this.status != DeliveryStatus.SHIPPED) {
            throw new IllegalStateException("출고 상태에서만 배송 완료 가능합니다.");
        }
        this.deliveredAt = LocalDateTime.now();
        this.status = DeliveryStatus.DELIVERED;
    }
}
