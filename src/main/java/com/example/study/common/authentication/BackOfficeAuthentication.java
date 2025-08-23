package com.example.study.common.authentication;

import lombok.Getter;

import java.util.UUID;

/**
 * 인증된 백오피스 회원의 정보를 담는 객체.
 */
@Getter
public class BackOfficeAuthentication {
    private final UUID backofficeMemberId;

    public BackOfficeAuthentication(UUID backofficeMemberId) {
        this.backofficeMemberId = backofficeMemberId;
    }
}
