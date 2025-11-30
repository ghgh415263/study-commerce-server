package com.example.study.common.authentication.fo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class ExpiredJwtTokenCleanupPollingScheduler {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    /**
     * ExpiredJwtTokenCleanupPollingScheduler
     *  DB에서 주기적으로 만료일자(expired_at)이 지난 JWT 데이터를 삭제한다.
     *  로그아웃시 로그아웃일자(logout_at)을 비교하기 때문에 fixedDelay를 빈번하게 설정하지 않아도 된다.
     *
     */
    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void expiredJwtTokenCleanup(){
        List<JwtBlacklist> expiredList = jwtBlacklistRepository.findByExpiredAtBefore(LocalDateTime.now());
        if(!expiredList.isEmpty()){
            try{
                jwtBlacklistRepository.deleteAllInBatch(expiredList);
            } catch (Exception e){
                log.error("로그인 만료 jwt 삭제 오류 ={}", e.getMessage());
            }
        }
    }
}
