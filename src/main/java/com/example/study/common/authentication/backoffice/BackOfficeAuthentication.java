package com.example.study.common.authentication.backoffice;

import lombok.Getter;

/**
 * 인증된 백오피스 회원의 정보를 담는 객체.
 */
@Getter
public class BackOfficeAuthentication {
    private final Long backofficeMemberId;

    public BackOfficeAuthentication(Long backofficeMemberId) {
        this.backofficeMemberId = backofficeMemberId;
    }
}
