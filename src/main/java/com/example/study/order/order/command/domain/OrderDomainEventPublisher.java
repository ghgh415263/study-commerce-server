package com.example.study.order.order.command.domain;

public interface OrderDomainEventPublisher {
    void publish(OrderDomainEvent event);
}
