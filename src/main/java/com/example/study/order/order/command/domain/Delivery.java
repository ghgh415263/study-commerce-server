package com.example.study.order.order.command.domain;

import com.example.study.common.InvalidArgumentException;
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
    @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배송 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    // 배송 주소
    @Column(nullable = false)
    private String address;

    // 배송 연락처 (전화번호)
    @Column(nullable = false, length = 20)
    private String contact;

    // 운송장 번호
    @Column(unique = true, length = 50)
    private String trackingNumber;

    @Column(unique = true)
    private Long orderId;

    // 배송 시작 일시
    private LocalDateTime shippedAt;

    // 배송 완료 일시
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    public Delivery(String address, String contact, Long orderId) {
        this.status = DeliveryStatus.NOT_STARTED;
        this.address = address;
        this.contact = contact;
        this.orderId = orderId;
    }

    public void addDeliveryItems(List<Long> orderItemIds) {
        if (this.status != DeliveryStatus.NOT_STARTED) {
            throw new InvalidArgumentException("NOT_STARTED 상태에서만 배송 아이템을 추가할 수 있습니다.");
        }

        for (Long orderItemId : orderItemIds) {
            boolean exists = deliveryItems.stream()
                    .anyMatch(i -> i.getOrderItemId().equals(orderItemId));

            if (exists) {
                throw new IllegalArgumentException("이미 추가된 OrderItemId입니다: " + orderItemId);
            }

            DeliveryItem deliveryItem = new DeliveryItem(orderItemId);
            deliveryItem.setDelivery(this);
            deliveryItems.add(deliveryItem);
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

    public void cancel() {
        if (this.status != DeliveryStatus.NOT_STARTED && this.status != DeliveryStatus.READY) {
            throw new InvalidDeliveryStateException("취소 할 수 없는 배달입니다.");
        }
        this.status =  DeliveryStatus.CANCELED;
    }
}
