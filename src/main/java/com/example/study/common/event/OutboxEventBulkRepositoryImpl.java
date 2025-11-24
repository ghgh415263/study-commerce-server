package com.example.study.common.event;

import com.example.study.common.authentication.fo.UnauthenticatedException;
import com.example.study.common.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxEventBulkRepositoryImpl implements OutboxEventBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    private final AuditorAware<String> auditorAware;

    public void saveAll(List<OutboxEvent> events) {
        if (events == null || events.isEmpty()) return;

        String currentAuditor = auditorAware.getCurrentAuditor()
                .orElseThrow(UnauthenticatedException::new);

        String sql = """
            INSERT INTO outbox_event (
                event_uuid, topic, partition_key, event_type, payload,
                status, occurred_at, sent_at, created_by, modified_by
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, events, events.size(), (ps, event) -> {
            ps.setBytes(1, UUIDUtils.toBytes(event.getEventUuid()));
            ps.setString(2, event.getTopic());
            ps.setString(3, event.getPartitionKey());
            ps.setString(4, event.getEventType());
            ps.setString(5, event.getPayload());
            ps.setString(6, event.getStatus().name());
            ps.setObject(7, event.getOccurredAt());
            ps.setObject(8, event.getSentAt());
            ps.setString(9, currentAuditor); // created_by
            ps.setString(10, currentAuditor); // modified_by
        });
    }
}