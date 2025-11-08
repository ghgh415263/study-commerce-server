package com.example.study.common.authentication.fo;

/**
 * <p>스레드 로컬(ThreadLocal)을 이용해 요청 처리 쓰레드마다 독립적인 사용자 정보를 저장합니다.</p>
 * <p>요청 처리 후 반드시 {@link #clear()}를 호출해 ThreadLocal을 정리해야
 * 메모리 누수 및 사용자 정보 오염을 방지할 수 있습니다.</p>
 */
public class AuthenticationHolder {

    private static final ThreadLocal<Authentication> loginIdHolder = new ThreadLocal<>();

    public static void set(Authentication authentication) {
        loginIdHolder.set(authentication);
    }

    public static Authentication get() {
        return loginIdHolder.get();
    }

    public static void clear() {
        loginIdHolder.remove();
    }

    // 생성 금지
    private AuthenticationHolder() {
    }
}
