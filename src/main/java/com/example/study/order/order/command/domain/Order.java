package com.example.study.order.order.command.domain;

import com.example.study.common.InvalidArgumentException;
import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Audited
@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUpdateEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    private LocalDateTime orderedAt; // 주문 일자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    public Order(Long memberId, LocalDateTime orderedAt, List<OrderItem> orderItems) {
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

    /**
     * 총 주문 금액 계산
     */
    public BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(item ->
                        item.getProductSnapshot()
                                .getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isCancelable() {
        if (orderStatus != OrderStatus.CREATED) {
            return false;
        }

        boolean containsCoupon = orderItems.stream()
                .anyMatch(OrderItem::isCoupon);
        return !containsCoupon;
    }

    public void cancel(){
        if (!isCancelable()) {
            throw new InvalidOrderStateException("현재 주문은 취소할 수 없습니다.");
        }
        // 2) 상태 변경
        this.orderStatus = OrderStatus.CANCELLED;
    }
}
