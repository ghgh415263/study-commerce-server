package com.example.study.common.authentication;

import lombok.Getter;

import java.util.UUID;

/**
 * 인증된 회원의 정보를 담는 객체.
 * 현재는 회원 식별용으로 memberId만 포함하고 있음.
 */
@Getter
public class Authentication {
    private final UUID memberId;

    public Authentication(UUID memberId) {
        this.memberId = memberId;
    }
}
