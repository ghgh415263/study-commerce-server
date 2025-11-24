package com.example.study.common.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 도메인 이벤트를 래핑하는 공통 Envelope 클래스.
 * 서비스 간 전달될 이벤트의 표준 메타데이터를 포함합니다.
 */
@Getter
public class DomainEventEnvelope<T extends DomainEvent> {

    private final UUID eventId = UUID.randomUUID();   // 고유 이벤트 ID
    private final String eventType;                   // 예: "CouponCreatedEvent"
    private final String aggregateType;               // 예: "Coupon"
    private final String aggregateId;                 // 예: 쿠폰 ID
    private final T payload;                          // 실제 도메인 이벤트 데이터
    private final String eventSource = "LegacyService"; // 이벤트 발행 주체
    private final Instant occurredAt = Instant.now(); // 이벤트 발생 시점 (epoch ms)

    public DomainEventEnvelope(String eventType, String aggregateType, String aggregateId, T payload) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
    }
}
