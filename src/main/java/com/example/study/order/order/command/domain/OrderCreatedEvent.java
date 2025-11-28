package com.example.study.order.order.command.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreatedEvent implements OrderDomainEvent {

    private final Long id;

    private final Long memberId;

    private final LocalDateTime orderedAt;

    private final List<OrderCreatedEventItem> orderItems;

    private final OrderStatus orderStatus;

    private final BigDecimal totalPrice;

    private final String couponIssueContact;

    public OrderCreatedEvent(BigDecimal totalPrice, Order order, String couponIssueContact) {
        this.id = order.getId();
        this.memberId = order.getMemberId();
        this.orderedAt = order.getOrderedAt();
        this.orderStatus = order.getOrderStatus();
        this.totalPrice = totalPrice;
        this.orderItems = order.getOrderItems().stream()
                .map(OrderCreatedEventItem::new)
                .toList();
        this.couponIssueContact = couponIssueContact;
    }

    @Getter
    public static class OrderCreatedEventItem {

        private final Long id;     // 이벤트용 명확한 이름
        private final Long productId;
        private final Integer quantity;
        private final BigDecimal priceAtOrder;
        private final String productType;
        private final String productName;   // ← 스냅샷에 들어갔으니 포함 가능

        public OrderCreatedEventItem(OrderItem orderItem) {
            this.id = orderItem.getId();
            this.quantity = orderItem.getQuantity();

            OrderedProductSnapshot snapshot = orderItem.getProductSnapshot();

            this.productId = orderItem.getProductId();
            this.productName = snapshot.getName();
            this.priceAtOrder = snapshot.getPrice();
            this.productType = snapshot.getProductType();
        }
    }
}
