package com.example.study.order.command.infra;

import com.example.study.common.lock.LockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MySQL 네임드 락을 사용하는 구현체입니다.
 */
@Slf4j
@Component
public class MysqlAppLockManager implements LockManager {

    private final JdbcTemplate jdbcTemplate;

    public MysqlAppLockManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean acquireLock(String lockName, int timeoutSeconds) {

        LocalDateTime now = LocalDateTime.now();
        // 1. 현재 락 조회
        String selectSql = "SELECT expire_at FROM app_lock WHERE lock_name = ?";
        List<LocalDateTime> results = jdbcTemplate.query(
                selectSql,
                (rs, rowNum) -> rs.getTimestamp("expire_at").toLocalDateTime(),
                lockName
        );

        if (!results.isEmpty()) {
            LocalDateTime existingExpireAt = results.get(0);
            if (existingExpireAt.isBefore(now)) {
                // TTL 지난 경우 삭제
                String deleteSql = "DELETE FROM app_lock WHERE lock_name = ?";
                jdbcTemplate.update(deleteSql, lockName);
            } else {
                // TTL 안 지난 경우 → 이미 진행 중
                return false;
            }
        }

        try {
            LocalDateTime expireAt = now.plusSeconds(timeoutSeconds);
            String insertSql = "INSERT INTO app_lock (lock_name, expire_at) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, lockName, expireAt);
            return true;
        }
        catch (Exception e) {
            log.warn("락 획득 실패", e);
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean releaseLock(String lockName) {
        String deleteSql = "DELETE FROM app_lock WHERE lock_name = ?";
        int rows = jdbcTemplate.update(deleteSql, lockName);
        return rows > 0;
    }
}
