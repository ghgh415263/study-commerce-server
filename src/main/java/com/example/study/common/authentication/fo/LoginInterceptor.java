package com.example.study.common.authentication.fo;

import com.example.study.common.util.AuthenticationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 인증된 사용자만 접근 가능한 요청에 대해 세션 로그인 상태를 검사하고,
 * 로그인된 사용자 ID를 {@link AuthenticationHolder}에 저장하는 인터셉터입니다.
 *
 * 요청이 끝나면 {@code ThreadLocal}에 저장된 사용자 정보를 정리합니다.
 */
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 요청이 컨트롤러에 도달하기 전에 호출되며,
     * 쿠키에 토큰 정보가 없을 경우 {@link UnauthenticatedException}을 발생시킵니다.
     * 로그인 토큰 정보가 있으면 {@link AuthenticationHolder}에 {@link Authentication}를 저장합니다.
     *
     * @return {@code true}일 경우 요청 처리를 계속 진행하고,
     *         {@code false}일 경우 처리를 중단합니다.
     * @throws UnauthenticatedException 로그인 세션이 없거나 인증 정보가 없을 때
     */
    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = AuthenticationUtils.extractTokenFromCookie(request);

        try {
            Authentication authentication = tokenManager.getAuthentication(token);
            AuthenticationHolder.set(authentication);
            return true;

        } catch (AuthenticationNotValidException jwtException) {

            response.addHeader("Set-Cookie", AuthenticationUtils.expireLoginCookie());

            throw jwtException;

        } catch (Exception e) {
            throw new UnauthenticatedException("유효하지 않은 토큰입니다.");
        }
    }

    /**
     * 요청 처리가 완료된 후 호출되며, {@link AuthenticationHolder}의 사용자 정보를 정리합니다.
     * 쓰레드풀을 사용하는 경우, 이전 사용자 정보가 남아있는 것을 방지하기 위해 반드시 호출되어야 합니다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AuthenticationHolder.clear();
    }
}
