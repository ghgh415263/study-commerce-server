package com.example.study.order.order.command.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity
) {}
