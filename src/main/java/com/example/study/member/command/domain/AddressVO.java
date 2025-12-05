package com.example.study.member.command.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AddressVO {

    private String zipCode;

    private String baseAddress;

    private String detailAddress;

    public AddressVO(String zipCode, String baseAddress, String detailAddress) {
        this.zipCode = zipCode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }
}
