package com.example.study.product.command.application.review;

import com.example.study.product.command.domain.review.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class tempUploadFileCleanupScheduler {

    private final UploadFileRepository uploadFileRepository;

    /**
     * 주기적으로 DB에서 매핑되지 않은 리뷰 이미지들을 삭제 한다.
     * */
    @Transactional
    @Scheduled(cron = "0 0 * * * *")  // 매 시간 정각 실행
    public void cleanupTempUploadFile() {
        LocalDateTime now = LocalDateTime.now();

        log.info("[tempUploadFile Cleanup] 시작: 시각={}", now);

        int totalDeleted = 0;

        while (true) {
            int deleted = uploadFileRepository.deleteTempUploadFile(now, 1000);
            totalDeleted += deleted;
            if (deleted < 1000) break;
        }

        log.info("[tempUploadFile Cleanup] 완료: {}개의 임시 업로드 파일 레코드를 삭제", totalDeleted);
    }
}
