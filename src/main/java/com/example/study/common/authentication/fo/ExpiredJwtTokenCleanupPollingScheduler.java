package com.example.study.common.authentication.fo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class ExpiredJwtTokenCleanupPollingScheduler {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    /**
     * 주기적으로 DB에서 만료(expired_at < now)된 JWT 블랙리스트 데이터를 삭제한다.
     *
     * batch size(1000개)로 끊어서 삭제하면 대량 데이터에서도 Lock/부하가 적음.
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * *")  // 매 시간 정각 실행
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();

        log.info("[JWT Cleanup] 시작: 시각={}", now);

        int totalDeleted = 0;

        while (true) {
            int deleted = jwtBlacklistRepository.deleteExpired(now, 1000);
            totalDeleted += deleted;
            if (deleted < 1000) break;
        }

        log.info("[JWT Cleanup] 완료: {}개의 만료된 JWT 레코드를 삭제", totalDeleted);
    }
}
