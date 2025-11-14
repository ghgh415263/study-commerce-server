package com.example.study.common.event;

import java.util.List;

public interface OutboxEventBulkRepository {

    void saveAll(List<OutboxEvent> events);
}