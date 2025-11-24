package com.example.study.order.order.command.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderPaymentDto(
        @Min(value = 1, message = "결제 금액은 1 이상이어야 합니다.")
        int amount,

        @NotNull(message = "결제 상태는 필수입니다.")
        String status
) {}
