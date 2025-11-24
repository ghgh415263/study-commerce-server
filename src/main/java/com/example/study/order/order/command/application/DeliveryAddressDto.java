package com.example.study.order.order.command.application;

import jakarta.validation.constraints.NotBlank;

public record DeliveryAddressDto(

        @NotBlank(message = "배송지 이름은 필수입니다.")
        String name,

        @NotBlank(message = "우편번호는 필수입니다.")
        String zipCode,

        @NotBlank(message = "기본 주소는 필수입니다.")
        String baseAddress,

        @NotBlank(message = "상세 주소는 필수입니다.")
        String detailAddress
) {}