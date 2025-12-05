package com.example.study.order.couponproductcoupon.command;

import com.example.study.common.event.DomainEventBundle;
import com.example.study.common.event.DomainEventEnvelope;
import com.example.study.common.event.OutboxEvent;
import com.example.study.common.event.OutboxEventBulkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponProductCouponEventPublisherImpl implements CouponProductCouponEventPublisher {

    private final OutboxEventBulkRepository outboxEventBulkRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public <T extends CouponProductCouponDomainEvent> void publish(String aggregateType, String aggregateId, List<T> eventPayloads) {

        if (eventPayloads == null || eventPayloads.isEmpty()) {
            log.debug("[CouponProductCouponEventPublisher] 발행할 이벤트가 비어있습니다.");
            return;
        }

        List<OutboxEvent> outboxEvents = convertToOutboxEvents(aggregateType, aggregateId, eventPayloads);

        outboxEventBulkRepository.saveAll(outboxEvents);
        log.info("[CouponProductCouponEventPublisher] Saved {} domain events for aggregateType={}, aggregateId={}",
                outboxEvents.size(), aggregateType, aggregateId);
    }

    @Transactional
    public <T extends CouponProductCouponDomainEvent> void publishBulk(List<DomainEventBundle<T>> bundles) {

        if (bundles == null || bundles.isEmpty()) {
            log.debug("[CouponProductCouponEventPublisher] 발행할 이벤트 번들 리스트가 비어있습니다.");
            return;
        }

        List<OutboxEvent> allOutboxEvents = new ArrayList<>();

        for (DomainEventBundle<T> bundle : bundles) {
            String aggregateType = bundle.aggregateType();
            String aggregateId = bundle.aggregateId();
            List<T> eventPayloads = bundle.events();

            if (eventPayloads == null || eventPayloads.isEmpty()) {
                continue;
            }

            List<OutboxEvent> outboxEvents = convertToOutboxEvents(aggregateType, aggregateId, eventPayloads);

            allOutboxEvents.addAll(outboxEvents);
        }

        if (!allOutboxEvents.isEmpty()) {
            outboxEventBulkRepository.saveAll(allOutboxEvents);
            log.info("[CouponProductCouponEventPublisher] Saved {} domain events across {} bundles",
                    allOutboxEvents.size(), bundles.size());
        } else {
            log.debug("[CouponProductCouponEventPublisher] No outbox events");
        }
    }

    private <T extends CouponProductCouponDomainEvent> List<OutboxEvent> convertToOutboxEvents(String aggregateType, String aggregateId, List<T> eventPayloads) {
        return eventPayloads.stream().map(eventPayload -> {
            try {
                String eventType = eventPayload.getClass().getSimpleName();

                DomainEventEnvelope<T> envelope = new DomainEventEnvelope<>(
                        eventType, aggregateType, aggregateId, eventPayload
                );

                String serializedPayload = objectMapper.writeValueAsString(envelope);
                String topic = aggregateType + ".events";

                return new OutboxEvent(
                        topic,
                        aggregateId,
                        eventType,
                        serializedPayload
                );

            } catch (JsonProcessingException e) {
                throw new EventPublishingFailedException("이벤트 직렬화에 실패했습니다. event=" + eventPayload, e);
            }
        }).toList();
    }
}
