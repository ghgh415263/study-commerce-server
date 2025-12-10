package com.example.study.order.order.command.application;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderDto(
        @NotBlank(message = "취소 사유 코드는 필수입니다.")
        String reasonCode,

        String detailReason
) {}
