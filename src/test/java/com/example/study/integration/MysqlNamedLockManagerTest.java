package com.example.study.integration;

import com.example.study.common.lock.MysqlAppLockManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 같은 세션에서만 락 해제가 가능하기 때문에,
 * @Transactional을 사용해 같은 트랜잭션(즉, 같은 세션) 안에서 테스트가 실행되도록 했습니다.
 * @JdbcTest에 @Transactional이 포함되어 있음.
 */
@JdbcTest
@Import(MysqlAppLockManager.class)
class MysqlNamedLockManagerTest {

	@Autowired
	private MysqlAppLockManager lockManager;

	@Test
	void testReleaseLock() {
		String lockName = "test_lock";

		assertTrue(lockManager.acquireLock(lockName, 1));
		assertTrue(lockManager.releaseLock(lockName));
	}

	/**
	 * 같은 트랜잭션 범위 내에서 다른 JdbcTemplate을 생성해
	 * 실제로 다른 세션에서 락 획득 시도가 어떻게 동작하는지 검증합니다.
	 */
	@Test
	void testAcquireLock_whenAlreadyLocked_thenFail() {

		String lockName = "conflict_lock";

		try {

			boolean firstLock = lockManager.acquireLock(lockName, 2);

			boolean secondLock = lockManager.acquireLock(lockName, 2);

			assertTrue(firstLock, "첫 번째 락 획득에 실패했습니다");
			assertFalse(secondLock, "두 번째 락은 실패해야 합니다 (이미 점유 중)");

		} finally {
			lockManager.releaseLock(lockName);
		}
	}

	/**
	 * 락이 expired 된 경우는 잘 획득할 수 있게 함
	 */
	@Test
	void testAcquireLock_whenExpired_thenSuccess() throws Exception {
		String lockName = "expired_lock";

		try {
			// 첫 번째 락 획득
			boolean firstLock = lockManager.acquireLock(lockName, 2);

			// TTL이 지나도록 대기 (2초 + 여유 시간)
			Thread.sleep(2500);

			boolean secondLock = lockManager.acquireLock(lockName, 1);

			assertTrue(firstLock, "첫 번째 락 획득에 실패했습니다");
			assertTrue(secondLock, "TTL 만료 후에는 다시 락을 획득할 수 있어야 합니다");
		} finally {
			lockManager.releaseLock(lockName);
		}
	}

	@Test
	void testAcquireLock_concurrentAccess_onlyOneSucceeds() throws Exception {
		String lockName = "concurrent_lock";
		int threadCount = 10;

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<Boolean>> results = new ArrayList<>();

		// 동시에 여러 스레드에서 락 획득 시도
		for (int i = 0; i < threadCount; i++) {
			results.add(executorService.submit(() -> lockManager.acquireLock(lockName, 5)));
		}

		// 모든 Future 완료 대기
		executorService.shutdown();

		long successCount = 0;
		long failCount = 0;

		for (Future<Boolean> future : results) {
			if (future.get()) {
				successCount++;
			} else {
				failCount++;
			}
		}

		// 하나의 스레드만 성공해야 함
		assertEquals(1, successCount, "동시 락 획득 시 오직 하나만 성공해야 합니다");
		assertEquals(threadCount - 1, failCount, "나머지는 모두 실패해야 합니다");

		// 테스트 종료 후 락 해제
		lockManager.releaseLock(lockName);
	}
}
