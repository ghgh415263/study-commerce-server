package com.example.study.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void pollAndSendOutboxEvents() {

        List<OutboxEvent> events = outboxEventRepository.findTop10ByStatusOrderByOccurredAtAsc(OutboxEventStatus.INIT);

        if (events.isEmpty()) return;

        for (OutboxEvent event : events) {

            try {
                // future 기다림
                kafkaTemplate.send(event.getTopic(), event.getPartitionKey(), event.getPayload())
                        .get(1, TimeUnit.SECONDS);

                // 2) send 성공 → SUCCESS
                event.changeStatus(OutboxEventStatus.SUCCESS);

            } catch (Exception e) {
                // 실패시 로그찍고 재시도 하기위해서 break 해버림
                log.error("outbox 발행 실패 id={}, 재시도할 것임", event.getId(), e);
                break;
            }
        }
    }
}
