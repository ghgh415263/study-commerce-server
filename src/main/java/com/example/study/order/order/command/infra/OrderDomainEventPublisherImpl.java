package com.example.study.order.order.command.infra;

import com.example.study.order.order.command.domain.OrderDomainEvent;
import com.example.study.order.order.command.domain.OrderDomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDomainEventPublisherImpl implements OrderDomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(OrderDomainEvent event) {
        publisher.publishEvent(event);
    }
}
