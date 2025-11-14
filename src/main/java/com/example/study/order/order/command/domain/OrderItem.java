package com.example.study.order.order.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Getter
@Entity
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal priceAtOrder; // 주문 당시 단가

    private String productType;

    private OrderItem(Long productId, int quantity, BigDecimal priceAtOrder, String productType) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.productType = productType;
    }

    public static OrderItem of(Long productId, int quantity, BigDecimal priceAtOrder, String productType) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if (priceAtOrder.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        return new OrderItem(productId, quantity, priceAtOrder, productType);
    }

    void setOrder(Order order) {
        this.order = order;
    }

}