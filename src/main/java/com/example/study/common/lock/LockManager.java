package com.example.study.common.lock;

/**
 * 네임드 락을 획득 및 해제하는 추상화 인터페이스입니다.
 */
public interface LockManager {

    /**
     * 지정한 이름의 락을 타임아웃 내에 획득 시도합니다.
     *
     * @param lockName 락 이름
     * @param timeoutSeconds 타임아웃(초)
     */
    boolean acquireLock(String lockName, int timeoutSeconds);

    /**
     * 지정한 이름의 락을 해제합니다.
     *
     * @param lockName 락 이름
     * @return 락 해제 성공 여부
     */
    boolean releaseLock(String lockName);
}
