package com.example.study.member.command.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DeliveryAddressRequestDto (

        @NotNull(message = "id는 필수입니다.")
        @Positive(message = "id는 0이나 음수가 될 수 없습니다.")
        Long id,

        @NotBlank(message = "배송지 이름은 필수입니다.")
        String name,

        @NotBlank(message = "우편번호는 필수입니다.")
        String zipCode,

        @NotBlank(message = "기본 주소는 필수입니다.")
        String baseAddress,

        @NotBlank(message = "상세 주소는 필수입니다.")
        String detailAddress
) {}

