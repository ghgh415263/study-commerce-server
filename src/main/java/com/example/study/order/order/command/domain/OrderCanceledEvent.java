package com.example.study.order.order.command.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCanceledEvent implements OrderDomainEvent{

    private final Long id;

    private final Long memberId;

    private final LocalDateTime orderedAt;

    private final List<OrderCanceledEventItem> orderItems;

    private final OrderStatus orderStatus;

    public OrderCanceledEvent(Order order) {
        this.id = order.getId();
        this.memberId = order.getMemberId();
        this.orderedAt = order.getOrderedAt();
        this.orderStatus = order.getOrderStatus();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderCanceledEventItem::new)
                .toList();
    }

    @Getter
    public static class OrderCanceledEventItem {
        private final Long id;
        private final Long productId;
        private final Integer quantity;

        public OrderCanceledEventItem(OrderItem orderItem) {
            this.id = orderItem.getId();
            this.productId = orderItem.getProductId();
            this.quantity = orderItem.getQuantity();
        }
    }
}
