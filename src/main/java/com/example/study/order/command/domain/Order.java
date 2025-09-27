package com.example.study.order.command.domain;

import com.example.study.common.InvalidArgumentException;
import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Audited
@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID memberId;

    private LocalDateTime orderedAt; // 주문 일자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    public Order(UUID memberId, LocalDateTime orderedAt, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new InvalidArgumentException("상품을 선택해야 주문이 가능합니다.");
        }
        this.memberId = memberId;
        this.orderedAt = orderedAt;
        this.orderStatus = OrderStatus.CREATED;
        orderItems.forEach(this::addOrderItem);
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}
