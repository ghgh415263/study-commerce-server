package com.example.study.common.event;

import com.example.study.common.InvalidArgumentException;

import java.util.List;

public record DomainEventBundle<T extends DomainEvent>(
        String aggregateType,
        String aggregateId,
        List<T> events
) {
    public DomainEventBundle {
        if (aggregateType == null || aggregateType.isBlank()) {
            throw new InvalidArgumentException("aggregateType은 null이거나 빈 문자열일 수 없습니다.");
        }
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new InvalidArgumentException("aggregateId는 null이거나 빈 문자열일 수 없습니다.");
        }
        if (events == null || events.isEmpty()) {
            throw new InvalidArgumentException("이벤트 목록(events)은 null이거나 비어 있을 수 없습니다.");
        }
    }
}
