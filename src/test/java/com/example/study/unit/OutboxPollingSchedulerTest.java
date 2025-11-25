package com.example.study.unit;

import com.example.study.common.event.OutboxEvent;
import com.example.study.common.event.OutboxEventRepository;
import com.example.study.common.event.OutboxEventStatus;
import com.example.study.common.event.OutboxPollingScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxPollingSchedulerTest {

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    OutboxPollingScheduler scheduler;

    @Test
    void 모든_이벤트가_정상적으로_전송() {
        // given
        OutboxEvent event1 = createMockOutboxEvent(1L);
        OutboxEvent event2 = createMockOutboxEvent(2L);

        when(outboxEventRepository.findTop10ByStatusOrderByOccurredAtAsc(OutboxEventStatus.INIT))
                .thenReturn(List.of(event1, event2));

        // send 성공 future
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // when
        scheduler.pollAndSendOutboxEvents();

        // then
        assertThat(OutboxEventStatus.SUCCESS).isEqualTo(event1.getStatus());
        assertThat(OutboxEventStatus.SUCCESS).isEqualTo(event2.getStatus());
        verify(kafkaTemplate, times(2))
                .send(anyString(), anyString(), anyString());
    }

    @Test
    void 두번째_메시지에서_실패하면_break되고_이후는_전송안됨() {
        // given
        OutboxEvent event1 = createMockOutboxEvent(1L);
        OutboxEvent event2 = createMockOutboxEvent(2L);
        OutboxEvent event3 = createMockOutboxEvent(3L);

        when(outboxEventRepository.findTop10ByStatusOrderByOccurredAtAsc(OutboxEventStatus.INIT))
                .thenReturn(List.of(event1, event2, event3));

        // 첫 번째는 성공, 두 번째는 실패
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null)) // event1 성공
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail"))); // event2 실패

        // when
        scheduler.pollAndSendOutboxEvents();

        // then
        assertThat(OutboxEventStatus.SUCCESS).isEqualTo(event1.getStatus());  // 1번 성공
        assertThat(OutboxEventStatus.INIT).isEqualTo(event2.getStatus()); // 실패 → 상태 변화 없음
        assertThat(OutboxEventStatus.INIT).isEqualTo(event3.getStatus());// 처리 안됨

        verify(kafkaTemplate, times(2))
                .send(anyString(), anyString(), anyString());
    }

    @Test
    void 이벤트가_없으면_바로리턴() {
        // given
        when(outboxEventRepository.findTop10ByStatusOrderByOccurredAtAsc(OutboxEventStatus.INIT))
                .thenReturn(List.of());

        // when
        scheduler.pollAndSendOutboxEvents();

        // then
        verify(kafkaTemplate, never())
                .send(anyString(), anyString(), anyString());
    }

    private OutboxEvent createMockOutboxEvent(Long id) {
        return new OutboxEvent(
                "orders",          // topic
                id + "",       // partitionKey
                "OrderCreated",    // eventType
                "{}"        // payload
        );
    }
}
