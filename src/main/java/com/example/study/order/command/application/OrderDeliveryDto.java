package com.example.study.order.command.application;

import jakarta.validation.constraints.NotBlank;

public record OrderDeliveryDto(
        @NotBlank(message = "배송 주소는 필수입니다.")
        String address,

        @NotBlank(message = "배송 연락처는 필수입니다.")
        String contact
) {}