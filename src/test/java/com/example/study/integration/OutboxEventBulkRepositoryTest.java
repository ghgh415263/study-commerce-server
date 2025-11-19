package com.example.study.integration;

import com.example.study.common.event.OutboxEvent;
import com.example.study.common.event.OutboxEventBulkRepository;
import com.example.study.common.event.OutboxEventBulkRepositoryImpl;
import com.example.study.common.persistance.JdbcConnectionDetailsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({TestPersistenceAuditorConfig.class, JdbcConnectionDetailsConfig.class})
public class OutboxEventBulkRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditorAware<String> auditorAware;

    private OutboxEventBulkRepository outboxEventBulkRepository;

    @BeforeEach
    void setUp() {
        outboxEventBulkRepository = new OutboxEventBulkRepositoryImpl(jdbcTemplate, auditorAware);
    }

    @Test
    void saveAll_shouldInsertAllEvents() {
        // given
        List<OutboxEvent> events = List.of(
                new OutboxEvent(
                        "order.events", "order-1",
                        "OrderCreatedEvent", "{\"id\":1}"
                ),
                new OutboxEvent(
                        "order.events", "order-2",
                        "OrderCreatedEvent", "{\"id\":2}"
                )
        );

        // when
        outboxEventBulkRepository.saveAll(events);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM outbox_event",
                Integer.class
        );
        assertThat(count).isEqualTo(2);
    }
}
