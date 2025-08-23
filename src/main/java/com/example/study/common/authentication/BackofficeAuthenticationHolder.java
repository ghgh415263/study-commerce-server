package com.example.study.common.authentication;

/**
 * 현재 백오피스에 로그인한 사용자의 ID를 요청 처리 중 보관하기 위한 유틸리티 클래스입니다.
 *
 * <p>스레드 로컬(ThreadLocal)을 이용해 요청 처리 쓰레드마다 독립적인 사용자 정보를 저장합니다.</p>
 * <p>요청 처리 후 반드시 {@link #clear()}를 호출해 ThreadLocal을 정리해야
 * 메모리 누수 및 사용자 정보 오염을 방지할 수 있습니다.</p>
 *
 * 이 클래스는 {@code package-private} 접근 제한으로
 * 같은 패키지 내에서만 사용하도록 설계되었습니다.
 * 외부 패키지(예: service, controller)에서는 직접 호출하지 말고,
 * {@link BackofficeAuthenticationContext}를 통해
 * 사용자 정보를 조회해야 합니다.
 */
class BackofficeAuthenticationHolder {

    private static final ThreadLocal<BackOfficeAuthentication> BackofficeAuthenticationHolder = new ThreadLocal<>();

    public static void set(BackOfficeAuthentication authentication) {
        BackofficeAuthenticationHolder.set(authentication);
    }

    public static BackOfficeAuthentication get() {
        return BackofficeAuthenticationHolder.get();
    }

    public static void clear() {
        BackofficeAuthenticationHolder.remove();
    }

    private BackofficeAuthenticationHolder() {
    }
}
