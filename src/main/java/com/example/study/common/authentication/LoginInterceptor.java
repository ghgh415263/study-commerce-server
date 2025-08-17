package com.example.study.common.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인증된 사용자만 접근 가능한 요청에 대해 세션 로그인 상태를 검사하고,
 * 로그인된 사용자 ID를 {@link LoginMemberHolder}에 저장하는 인터셉터입니다.
 *
 * 요청이 끝나면 {@code ThreadLocal}에 저장된 사용자 정보를 정리합니다.
 *
 * <p>이 인터셉터는 컨트롤러 진입 전에 세션에 로그인 정보가 없을 경우 예외를 발생시키고,
 * 이후 로직에서는 {@link LoginMemberContext}를 통해 사용자 ID에 접근할 수 있도록 합니다.</p>
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final String LOGIN_SESSION_KEY = "loginId";

    /**
     * 요청이 컨트롤러에 도달하기 전에 호출되며,
     * 세션에 로그인 정보가 없을 경우 {@link UnauthenticatedException}을 발생시킵니다.
     * 로그인 정보가 있으면 {@link LoginMemberHolder}에 사용자 ID를 저장합니다.
     *
     * @return {@code true}일 경우 요청 처리를 계속 진행하고,
     *         {@code false}일 경우 처리를 중단합니다.
     * @throws UnauthenticatedException 로그인 세션이 없거나 인증 정보가 없을 때
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LOGIN_SESSION_KEY) == null) {
            throw new UnauthenticatedException();
        }

        String loginId = (String) session.getAttribute(LOGIN_SESSION_KEY);
        LoginMemberHolder.set(loginId);
        return true;
    }

    /**
     * 요청 처리가 완료된 후 호출되며, {@link LoginMemberHolder}의 사용자 정보를 정리합니다.
     * 쓰레드풀을 사용하는 경우, 이전 사용자 정보가 남아있는 것을 방지하기 위해 반드시 호출되어야 합니다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LoginMemberHolder.clear();
    }
}
