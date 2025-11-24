package com.example.study.order.order.command.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreatedEvent implements OrderDomainEvent {

    private Long id;

    private Long memberId;

    private LocalDateTime orderedAt;

    private List<OrderCreatedEventItem> orderItems;

    private OrderStatus orderStatus;

    private BigDecimal totalPrice;

    private String couponIssueContact;

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
        private Long id;
        private Long productId;
        private Integer quantity;
        private BigDecimal priceAtOrder;
        private String productType;

        public OrderCreatedEventItem(OrderItem orderItem) {
            this.id = orderItem.getId();
            this.productId = orderItem.getProductId();
            this.quantity = orderItem.getQuantity();
            this.priceAtOrder = orderItem.getPriceAtOrder();
            this.productType = orderItem.getProductType();
        }
    }
}
