package com.example.study.common.authentication;

import org.springframework.stereotype.Component;

/**
 * 현재 요청 처리 중인 로그인한 백오피스 사용자의 ID를 제공하는 컴포넌트입니다.
 *
 * {@link BackofficeLoginMemberHolder}에 저장된 ThreadLocal 기반 로그인 ID를 읽기 전용으로 노출합니다.
 * 서비스나 컨트롤러 등 애플리케이션 계층에서는 이 빈을 주입받아
 * 직접 {@link BackofficeLoginMemberHolder}를 사용하는 대신 안전하게 백오피스dp 로그인한 ID를 조회할 수 있습니다.
 *
 * <p><strong>주의:</strong> 이 클래스는 로그인 ID를 조회만 담당하며,
 * 설정(set)이나 삭제(clear) 기능은 제공하지 않습니다.
 **/
@Component
public class BackofficeLoginMemberContext {

    public String getLoginId() {
        return BackofficeLoginMemberHolder.get();
    }
}

