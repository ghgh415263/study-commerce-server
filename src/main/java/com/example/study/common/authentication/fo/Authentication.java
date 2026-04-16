package com.example.study.common.authentication.fo;

import lombok.Getter;

/**
 * 인증된 회원의 정보를 담는 객체.
 * 현재는 회원 식별용으로 memberId만 포함하고 있음.
 */
@Getter
public class Authentication {
    private final Long memberId;

    public Authentication(Long memberId) {
        this.memberId = memberId;
    }

    public boolean isAnonymous() {
        return memberId == null;
    }

    public static Authentication createAnonymousAuthentication(){
        return new Authentication(null);
    }
}
