package com.example.study.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Slf4j
@Component
public class LockTemplate {

    private final LockManager lockManager;
    private final int timeoutSeconds;

    public LockTemplate(LockManager lockManager) {
        this.lockManager = lockManager;
        this.timeoutSeconds = 2;
    }

    /**
     * 지정한 락 이름으로 락을 획득한 후 supplier의 작업을 실행한다.
     * 락을 획득한 상태에서 supplier.get()을 호출하며, 작업 종료 후 반드시 락을 해제한다.
     *
     * @param lockName 락 이름 (null 또는 공백일 수 없음)
     * @param supplier 락 획득 후 실행할 작업을 공급하는 Supplier 함수형 인터페이스
     * @param <T> supplier가 반환하는 결과 타입
     * @return supplier가 반환하는 결과 값
     * @throws IllegalArgumentException lockName이 null이거나 빈 문자열인 경우
     * @throws RuntimeException 락 획득 실패 시 발생
     */
    @Transactional
    public <T> T executeWithLock(String lockName, Supplier<T> supplier) {
        if (lockName == null || lockName.isBlank()) {
            throw new IllegalArgumentException("lockName은 필수값입니다.");
        }

        boolean locked = lockManager.acquireLock(lockName, timeoutSeconds);
        if (!locked) {
            throw new LockAcquisitionFailureException("락 획득 실패: " + lockName);
        }

        try {
            return supplier.get();
        } finally {
            boolean unlocked = lockManager.releaseLock(lockName);
            if (!unlocked) {
                log.warn("락 해제에 실패했습니다: {}", lockName);
            }
        }
    }
}
