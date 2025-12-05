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
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Embedded
    private OrderedProductSnapshot productSnapshot;

    @Column(nullable = false)
    private int quantity;

    private OrderItem(OrderedProductSnapshot productSnapshot, int quantity, Long productId) {
        this.productSnapshot = productSnapshot;
        this.quantity = quantity;
        this.productId = productId;
    }

    public static OrderItem of(Long productId,
                               String productName,
                               BigDecimal priceAtOrder,
                               String productType,
                               int quantity) {

        if (quantity < 1)
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        OrderedProductSnapshot snapshot = OrderedProductSnapshot.of(productName, priceAtOrder, productType);

        return new OrderItem(snapshot, quantity, productId);
    }

    void setOrder(Order order) {
        this.order = order;
    }
}