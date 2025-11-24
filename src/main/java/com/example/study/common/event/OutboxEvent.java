package com.example.study.common.event;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "outbox_event",
		indexes = {
                @Index(name = "idx_outbox_status_occurredat", columnList = "status, occurredAt"),
                @Index(name = "idx_outbox_occurredat", columnList = "occurredAt")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID eventUuid;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String partitionKey;

    @Column(nullable = false)
    private String eventType;

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status = OutboxEventStatus.INIT;

    private LocalDateTime occurredAt;

    private LocalDateTime sentAt;

    public OutboxEvent(String topic, String partitionKey, String eventType, String payload) {
        this.topic = topic;
        this.partitionKey = partitionKey;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = LocalDateTime.now();
        this.eventUuid = UUID.randomUUID();
    }

    public void changeStatus(OutboxEventStatus newStatus) {
        if (this.status == OutboxEventStatus.SUCCESS) {
            throw new OutboxEventAlreadyFinalizedException("이미 발송 성공 상태입니다.");
        }
        this.status = newStatus;
        if (newStatus == OutboxEventStatus.SUCCESS) {
            this.sentAt = LocalDateTime.now();
        }
    }
}
