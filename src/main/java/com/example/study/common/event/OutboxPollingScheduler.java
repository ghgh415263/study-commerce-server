package com.example.study.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class OutboxPollingScheduler {

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Outbox Polling Scheduler
     *
     * 단일 서버 환경에서는 @Scheduled 기반의 단순 Polling 방식으로도 충분함.
     * 하지만 추후 서버가 멀티 인스턴스 환경으로 확장되면,
     * 배치 처리(SPRING BATCH)로 안정적인 청크 단위 처리
     * 혹은 Debezium CDC 기반 Outbox 패턴
     *
     * 단일 서버 환경에서는 현재 구조가 가장 단순하고 안정적이므로 유지함.
     */
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void pollAndSendOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findTop10ByStatusOrderByOccurredAtAsc(OutboxEventStatus.INIT);

        for (OutboxEvent event : events) {
            try {
                // Kafka 전송
                kafkaTemplate.send(event.getTopic(), event.getPartitionKey(), event.getPayload());

                // 상태 변경
                event.changeStatus(OutboxEventStatus.SUCCESS);
                log.info("Sent outbox event: {}", event.getId());
            } catch (Exception e) {
                event.changeStatus(OutboxEventStatus.FAIL);
                log.error("Failed to send outbox event: {}", event.getId(), e);
            }
        }
    }
}
